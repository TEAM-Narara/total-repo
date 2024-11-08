package com.narara.superboard.memberbackground.interfaces;

import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.common.interfaces.response.ResponseMessage;
import com.narara.superboard.common.interfaces.response.StatusCode;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.memberbackground.entity.MemberBackground;
import com.narara.superboard.memberbackground.interfaces.dto.MemberBackgroundResponseDto;
import com.narara.superboard.memberbackground.service.MemberBackgroundService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "멤버 배경")
@Controller
@RequiredArgsConstructor
public class MemberBackgroundController implements MemberBackgroundAPI {

    private final MemberBackgroundService memberBackgroundService;

    @Override
    public ResponseEntity<DefaultResponse<MemberBackgroundResponseDto>> addMemberBackground(
            @RequestParam Long memberId,
            @RequestParam String imgUrl) {

        MemberBackground memberBackground = memberBackgroundService.addMemberBackground(memberId, imgUrl);
        return new ResponseEntity<>(
                (DefaultResponse.res(StatusCode.CREATED, ResponseMessage.MEMBER_BACKGROUND_ADD_SUCCESS,
                        MemberBackgroundResponseDto.of(memberBackground))),
                HttpStatus.CREATED
        );
    }

    @Override
    public ResponseEntity<DefaultResponse<List<MemberBackgroundResponseDto>>> getAllMemberBackground(
            @AuthenticationPrincipal Member member) {

        List<MemberBackground> backgrounds = memberBackgroundService.getAllMemberBackground(member);
        List<MemberBackgroundResponseDto> memberBackgroundResponseDtoList= new ArrayList<>();
        for (MemberBackground background : backgrounds) {
            memberBackgroundResponseDtoList.add(MemberBackgroundResponseDto.of(background));
        }
        return new ResponseEntity<>(
                DefaultResponse.res(StatusCode.OK, ResponseMessage.MEMBER_BACKGROUND_LIST_FETCH_SUCCESS,
                        memberBackgroundResponseDtoList),
                HttpStatus.OK
        );
    }

    @Override
    public ResponseEntity<DefaultResponse<Void>> deleteMemberBackground(
            @AuthenticationPrincipal Member member,
            @PathVariable Long backgroundId) {

        memberBackgroundService.deleteMemberBackground(member, backgroundId);
        return new ResponseEntity<>(
                DefaultResponse.res(StatusCode.OK, ResponseMessage.MEMBER_BACKGROUND_DELETE_SUCCESS),
                HttpStatus.OK
        );
    }
}
