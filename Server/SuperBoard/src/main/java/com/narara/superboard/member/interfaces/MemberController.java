package com.narara.superboard.member.interfaces;

import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.common.interfaces.response.ResponseMessage;
import com.narara.superboard.common.interfaces.response.StatusCode;
import com.narara.superboard.common.service.IAuthenticationFacade;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.interfaces.dto.MemberResponseDto;
import com.narara.superboard.member.interfaces.dto.MemberUpdateRequestDto;
import com.narara.superboard.member.interfaces.dto.SearchMemberListResponseDto;
import com.narara.superboard.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController implements MemberAPI {

    private final MemberService memberService;
    private final IAuthenticationFacade authenticationFacade;
    @Override
    public ResponseEntity<?> getMember() {
        Long memberId = authenticationFacade.getAuthenticatedUser().getUserId();
        MemberResponseDto member = memberService.getMember(memberId);

        return new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.READ_USER,member), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> updateMember(MemberUpdateRequestDto memberUpdateRequestDto) {
        Long memberId = authenticationFacade.getAuthenticatedUser().getUserId();
        MemberResponseDto member = memberService.updateMember(memberId,memberUpdateRequestDto);

        return new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.UPDATE_USER,member), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> searchMember(String searchTerm, Pageable pageable) {
        SearchMemberListResponseDto searchMemberListResponseDto
                = memberService.searchMember(searchTerm,pageable);

        return new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.SEARCH_USERS,searchMemberListResponseDto), HttpStatus.OK);
    }
}
