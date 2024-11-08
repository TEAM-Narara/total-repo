package com.narara.superboard.listmember.interfaces;

import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.listmember.interfaces.dto.AlertDto;
import com.narara.superboard.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/list-member")
public interface ListMemberAPI {

    @PatchMapping("/{listId}/alert/{memberId}")
    @Operation(summary = "리스트 멤버 알림 설정", description = "특정 리스트에서 멤버의 알림 상태를 설정합니다.")
    ResponseEntity<DefaultResponse<AlertDto>> setListMemberIsAlert(
            @AuthenticationPrincipal Member member,
            @PathVariable Long listId);

    @GetMapping("/{listId}/alert")
    @Operation(summary = "자신의 리스트 알림 조회", description = "특정 리스트에서 자신의 알림 상태를 확인합니다.")
    ResponseEntity<DefaultResponse<AlertDto>> getListMemberIsAlert(
            @AuthenticationPrincipal Member member,
            @PathVariable Long listId);
}
