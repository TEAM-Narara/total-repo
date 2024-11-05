package com.narara.superboard.member.interfaces;

import com.narara.superboard.member.interfaces.dto.MemberUpdateRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "회원", description = "멤버 관련 API")
@RequestMapping("/api/v1/members")
public interface MemberAPI {

    // 유저 정보 조회
    @GetMapping()
    @Operation(summary = "유저 정보 조회")
    ResponseEntity<?> getMember();

    // 유저 정보 수정
    @PatchMapping
    @Operation(summary = "유저 정보 수정")
    ResponseEntity<?> updateMember(MemberUpdateRequestDto memberUpdateRequestDto);

    // 유저 검색
    @GetMapping("/search")
    @Operation(summary = "유저 검색")
    ResponseEntity<?> searchMember(@RequestParam String searchTerm, Pageable pageable);
}
