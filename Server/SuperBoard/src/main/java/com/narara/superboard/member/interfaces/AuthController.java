package com.narara.superboard.member.interfaces;

import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.common.interfaces.response.ResponseMessage;
import com.narara.superboard.common.interfaces.response.StatusCode;
import com.narara.superboard.member.interfaces.dto.MemberCreateRequestDto;
import com.narara.superboard.member.interfaces.dto.MemberLoginRequestDto;
import com.narara.superboard.member.interfaces.dto.TokenDto;
import com.narara.superboard.member.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthAPI{
    private final AuthService authService;

    @Override
    public ResponseEntity<?> login(@RequestBody MemberLoginRequestDto memberLoginRequestDto) {
        try{
            TokenDto tokens = authService.login(memberLoginRequestDto);
            // 헤더에 토큰 넣기
            // 헤더에 AccessToken 추가

            System.out.println(tokens.accessToken());
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + tokens.accessToken()); // 헤더에 Authorization 추가

            return new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.LOGIN_SUCCESS,tokens.accessToken()), HttpStatus.OK);

        }catch (BadCredentialsException e) {
            // 인증 실패 시 에러 메시지와 상태코드 반환
            return new ResponseEntity<>(DefaultResponse.res(StatusCode.UNAUTHORIZED, "Invalid credentials"), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            // 내부 서버 에러 처리
            return new ResponseEntity<>(DefaultResponse.res(StatusCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> register(@RequestBody MemberCreateRequestDto memberCreateRequestDto) {
        try{
            authService.register(memberCreateRequestDto);
            return new ResponseEntity<>(DefaultResponse.res(StatusCode.CREATED, ResponseMessage.CREATED_USER), HttpStatus.CREATED);
        }catch(Exception e){
            return new ResponseEntity<>(DefaultResponse.res(StatusCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
