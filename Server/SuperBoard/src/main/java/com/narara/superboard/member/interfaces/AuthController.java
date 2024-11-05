package com.narara.superboard.member.interfaces;

import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.common.interfaces.response.ResponseMessage;
import com.narara.superboard.common.interfaces.response.StatusCode;
import com.narara.superboard.common.service.IAuthenticationFacade;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.interfaces.dto.MemberCreateRequestDto;
import com.narara.superboard.member.interfaces.dto.MemberLoginRequestDto;
import com.narara.superboard.member.interfaces.dto.MemberLoginResponseDto;
import com.narara.superboard.member.interfaces.dto.TokenDto;
import com.narara.superboard.member.service.AuthService;
import com.narara.superboard.member.service.OAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthAPI{

    private final AuthService authService;
    @Autowired
    private final IAuthenticationFacade authenticationFacade;
    private OAuth2UserService oAuth2UserService;

    @Override
    public ResponseEntity<?> login(@RequestBody MemberLoginRequestDto memberLoginRequestDto) {
        MemberLoginResponseDto memberLoginResponseDto = authService.login(memberLoginRequestDto);
        TokenDto tokens = memberLoginResponseDto.tokenDto();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + tokens.accessToken());
        headers.add("Refresh-Token", tokens.refreshToken());

        return new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.LOGIN_SUCCESS,memberLoginResponseDto.memberDto()), headers,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> register(@RequestBody MemberCreateRequestDto memberCreateRequestDto) {
        MemberLoginResponseDto memberLoginResponseDto = authService.register(memberCreateRequestDto);
        TokenDto tokens = memberLoginResponseDto.tokenDto();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + tokens.accessToken());
        headers.add("Refresh-Token", tokens.refreshToken());

        return new ResponseEntity<>(DefaultResponse.res(StatusCode.CREATED, ResponseMessage.CREATED_USER,memberLoginResponseDto.memberDto()),headers, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> oauth2Login(String accessToken, String provider) {
        MemberLoginResponseDto memberLoginResponseDto = oAuth2UserService.getUserInfo(accessToken, provider);

        // 위의 소셜의 사용자 데이터를 가지고 토큰 발급
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + memberLoginResponseDto.tokenDto().accessToken());
        headers.add("Refresh-Token", memberLoginResponseDto.tokenDto().refreshToken());

        return new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.LOGIN_SUCCESS,memberLoginResponseDto.memberDto()), headers,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> logout() {
        Long memberId = authenticationFacade.getAuthenticatedUser().getUserId();
        authService.logout(memberId);

        return new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.LOGOUT_SUCCESS), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> withdrawal() {
        Long memberId = authenticationFacade.getAuthenticatedUser().getUserId();
        authService.withdrawal(memberId);

        return new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.WITHDRAWAL_USER), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> reissueAccessToken(@RequestHeader("Refresh-Token") String refreshToken) {
        String accessToken = authService.reissueAccessToken(refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Refresh-Token", refreshToken);

        return new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.REISSUE_ACCESSTOKEN), headers,HttpStatus.OK);
    }
}
