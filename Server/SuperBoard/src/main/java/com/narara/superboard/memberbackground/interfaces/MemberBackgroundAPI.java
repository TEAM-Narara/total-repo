package com.narara.superboard.memberbackground.interfaces;

import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.memberbackground.entity.MemberBackground;
import com.narara.superboard.memberbackground.interfaces.dto.MemberBackgroundResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/member-background")
public interface MemberBackgroundAPI {

    @PostMapping
    @Operation(summary = "회원 배경 추가", description = "회원의 배경 이미지를 추가합니다.")
    ResponseEntity<DefaultResponse<MemberBackgroundResponseDto>> addMemberBackground(
            @RequestParam Long memberId,
            @RequestParam String imgUrl);

    @GetMapping("/{memberId}")
    @Operation(summary = "회원 배경 리스트 조회", description = "회원의 모든 배경 이미지를 조회합니다.")
    ResponseEntity<DefaultResponse<List<MemberBackgroundResponseDto>>> getAllMemberBackground(
            @AuthenticationPrincipal Member member);

    @DeleteMapping("/{memberId}/{backgroundId}")
    @Operation(summary = "회원 배경 삭제", description = "특정 배경 이미지를 삭제합니다.")
    ResponseEntity<DefaultResponse<Void>> deleteMemberBackground(
            @AuthenticationPrincipal Member member,
            @PathVariable Long backgroundId);
}
