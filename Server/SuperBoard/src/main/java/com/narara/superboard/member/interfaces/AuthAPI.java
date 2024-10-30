package com.narara.superboard.member.interfaces;

import com.narara.superboard.common.entity.CustomUserDetails;
import com.narara.superboard.member.interfaces.dto.MemberCreateRequestDto;
import com.narara.superboard.member.interfaces.dto.MemberLoginRequestDto;
import com.narara.superboard.member.interfaces.dto.TokenDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "AUTH")
@RequestMapping("/api/v1/members")
public interface AuthAPI {

    @PostMapping("/login")
    ResponseEntity<?> login(MemberLoginRequestDto memberLoginRequestDto);

    @PostMapping("/register")
    ResponseEntity<?> register(MemberCreateRequestDto memberCreateRequestDto);

    @GetMapping("/logout")
    ResponseEntity<?> logout();

    @GetMapping("/withdrawal")
    ResponseEntity<?> withdrawal();

    @GetMapping("/reissue")
    ResponseEntity<?> reissueAccessToken(@RequestHeader("Refresh-Token") String refreshToken);

}
