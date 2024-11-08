package com.narara.superboard.member.service;

import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.exception.MemberNotFoundException;
import com.narara.superboard.member.infrastructure.MemberRepository;
import com.narara.superboard.member.interfaces.dto.MemberResponseDto;
import com.narara.superboard.member.interfaces.dto.MemberUpdateRequestDto;
import com.narara.superboard.member.interfaces.dto.SearchMemberListResponseDto;
import com.narara.superboard.member.interfaces.dto.SearchMemberResponseDto;
import com.narara.superboard.member.service.validator.MemberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MemberValidator memberValidator;

    @Override
    public MemberResponseDto getMember(Long memberId) {
        Member member = findMemberByIdOrThrow(memberId);
        return MemberResponseDto.from(member);
    }

    @Override
    public MemberResponseDto updateMember(Long memberId, MemberUpdateRequestDto requestDto) {
        memberValidator.validateNickname(requestDto.nickname());

        Member updatedMember = findMemberByIdOrThrow(memberId)
                .copyWithUpdatedFields(requestDto.nickname(), requestDto.profileImgUrl());

        return MemberResponseDto.from(memberRepository.save(updatedMember));
    }

    @Override
    public SearchMemberListResponseDto searchMember(String searchTerm, Pageable pageable) {
        memberValidator.validateSearchTerm(searchTerm);

        Page<Member> membersPage = memberRepository
                .findByNicknameContainingIgnoreCaseOrEmailContainingIgnoreCase(searchTerm, searchTerm, pageable);

        List<SearchMemberResponseDto> memberDtos = membersPage.stream()
                .map(this::mapToSearchMemberDto)
                .toList();

        return buildSearchResponse(membersPage, memberDtos);
    }

    private Member findMemberByIdOrThrow(Long memberId) {
        return memberRepository.findByIdAndIsDeletedFalse(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
    }

    private SearchMemberResponseDto mapToSearchMemberDto(Member member) {
        return new SearchMemberResponseDto(
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                member.getProfileImgUrl()
        );
    }

    private SearchMemberListResponseDto buildSearchResponse(Page<Member> page, List<SearchMemberResponseDto> memberDtos) {
        return SearchMemberListResponseDto.builder()
                .searchMemberResponseDtoList(memberDtos)
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .build();
    }
}
