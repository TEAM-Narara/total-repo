package com.narara.superboard.member.service;

import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.interfaces.dto.MemberResponseDto;
import com.narara.superboard.member.interfaces.dto.MemberUpdateRequestDto;
import com.narara.superboard.member.interfaces.dto.SearchMemberListResponseDto;
import org.springframework.data.domain.Pageable;

public interface MemberService {
    // 유저 정보 조회
    MemberResponseDto getMember(Long memberId);
    // 유저 정보 수정
    MemberResponseDto updateMember(Long memberId,MemberUpdateRequestDto memberUpdateRequestDto);
    // 유저 검색
    SearchMemberListResponseDto searchMember(String searchTerm, Pageable pageable);
}
