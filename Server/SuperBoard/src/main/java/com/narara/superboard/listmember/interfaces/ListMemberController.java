package com.narara.superboard.listmember.interfaces;

import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.common.interfaces.response.ResponseMessage;
import com.narara.superboard.common.interfaces.response.StatusCode;
import com.narara.superboard.listmember.service.ListMemberService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Tag(name = "리스트 멤버")
@Controller
@RequiredArgsConstructor
public class ListMemberController implements ListMemberAPI {

    private final ListMemberService listMemberService;

    @Override
    public ResponseEntity<DefaultResponse<Void>> setListMemberIsAlert(
            @PathVariable Long memberId,
            @PathVariable Long listId) {

        listMemberService.setListMemberIsAlert(memberId, listId);
        return ResponseEntity.ok(
                DefaultResponse.res(StatusCode.OK, ResponseMessage.LIST_MEMBER_ALERT_STATUS_UPDATE_SUCCESS)
        );
    }
}
