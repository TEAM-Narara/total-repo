package com.narara.superboard.listmember.interfaces;

import com.narara.superboard.common.interfaces.response.DefaultResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/list-member")
public interface ListMemberAPI {

    @PatchMapping("/{listId}/alert/{memberId}")
    @Operation(summary = "리스트 멤버 알림 설정", description = "특정 리스트에서 멤버의 알림 상태를 설정합니다.")
    ResponseEntity<DefaultResponse<Void>> setListMemberIsAlert(
            @PathVariable Long memberId,
            @PathVariable Long listId);
}
