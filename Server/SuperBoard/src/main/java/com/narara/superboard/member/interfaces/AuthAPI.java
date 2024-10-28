package com.narara.superboard.member.interfaces;

import com.narara.superboard.member.interfaces.dto.MemberCreateRequestDto;
import com.narara.superboard.member.interfaces.dto.MemberLoginRequestDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "AUTH")
@RequestMapping("/api/v1/auth")
public interface AuthAPI {

    @PostMapping("/login")
    ResponseEntity<?> login(MemberLoginRequestDto memberLoginRequestDto);

    @PostMapping("/register")
    ResponseEntity<?> register(MemberCreateRequestDto memberCreateRequestDto);
}
