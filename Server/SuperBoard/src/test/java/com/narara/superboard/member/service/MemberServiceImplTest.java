package com.narara.superboard.member.service;

import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.exception.MemberNicknameNotFoundException;
import com.narara.superboard.member.exception.MemberNotFoundException;
import com.narara.superboard.member.exception.SearchTermNotFoundException;
import com.narara.superboard.member.infrastructure.MemberRepository;
import com.narara.superboard.member.interfaces.dto.MemberResponseDto;
import com.narara.superboard.member.interfaces.dto.MemberUpdateRequestDto;
import com.narara.superboard.member.interfaces.dto.SearchMemberListResponseDto;
import com.narara.superboard.member.service.validator.MemberValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class MemberServiceImplTest {
    @InjectMocks
    private MemberServiceImpl memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberValidator memberValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * 유저 조회 TEST ----------------------------------------------------------------------
     */
    // 1. 성공 테스트
    @Test
    @DisplayName("회원 조회 성공 테스트")
    void testGetMemberById_Success() {
        // 1. 성공 테스트
        // Given
        Long memberId = 1L;
        Member member = new Member(memberId, "testUser", "test@example.com");

        // When
        when(memberRepository.findByIdAndIsDeletedFalse(memberId)).thenReturn(Optional.of(member));

        // Then
        MemberResponseDto result = memberService.getMember(memberId);

        assertNotNull(result);
        assertEquals(memberId, result.memberId());
        assertEquals("testUser", result.nickname());
    }

    @Test
    @DisplayName("회원 조회 실패 테스트 - 유저를 찾지 못하는 경우")
    void testGetMemberById_Failure() {
        // Given
        Long memberId = 1L;

        // When
        when(memberRepository.findByIdAndIsDeletedFalse(memberId)).thenReturn(Optional.empty());

        // Then
        assertThrows(MemberNotFoundException.class, () -> memberService.getMember(memberId));
    }

    /**
     * 유저 정보 수정 TEST ----------------------------------------------------------------------
     */

    @Test
    @DisplayName("회원 정보 수정 성공 테스트")
    void testUpdateMember_Success() {
        // Given
        Long memberId = 1L;
        Member existingMember = new Member(memberId, "oldUser", "old@example.com");
        MemberUpdateRequestDto updateRequest = new MemberUpdateRequestDto("updatedUser", "updated@example.com");
        Member updatedMember = new Member(memberId, updateRequest.nickname(),existingMember.getEmail() ,updateRequest.profileImgUrl());

        // When
        when(memberRepository.findByIdAndIsDeletedFalse(memberId)).thenReturn(Optional.of(existingMember));
        when(memberRepository.save(any(Member.class))).thenReturn(updatedMember);

        // Then
        MemberResponseDto result = memberService.updateMember(memberId,updateRequest);

        assertNotNull(result);
        assertEquals(updateRequest.nickname(), result.nickname());
        assertEquals(updateRequest.profileImgUrl(), result.profileImgUrl());
    }

    @Test
    @DisplayName("회원 정보 수정 실패 테스트 - 닉네임이 null인 경우")
    void testUpdateMember_Failure_NullValues() {
        // Given
        Long memberId = 1L;
        MemberUpdateRequestDto updateRequest = new MemberUpdateRequestDto(null, "ㅇㅇㅇㅇ"); // 닉네임이 null

        // When
        doThrow(new MemberNicknameNotFoundException()).when(memberValidator).validateNickname(updateRequest.nickname());

        // Then
        assertThrows(MemberNicknameNotFoundException.class, () -> memberService.updateMember(memberId,updateRequest));
    }

    @Test
    @DisplayName("회원 정보 수정 실패 테스트 - 유저를 찾지 못하는 경우")
    void testUpdateMember_Failure_MemberNotFound() {
        // Given
        Long memberId = 1L;
        MemberUpdateRequestDto updateRequest = new MemberUpdateRequestDto("updatedUser", "updated@example.com");

        // When
        when(memberRepository.findByIdAndIsDeletedFalse(memberId)).thenReturn(Optional.empty());

        // Then
        assertThrows(MemberNotFoundException.class, () -> memberService.updateMember(memberId,updateRequest));
    }

    /**
     * 멤버 검색 TEST ----------------------------------------------------------------------
     */
    @Test
    @DisplayName("회원 검색 성공 테스트 - 검색어가 닉네임 또는 이메일에 포함된 경우")
    void testSearchMember_Success() {
        // Given
        String searchTerm = "test";
        Pageable pageable = PageRequest.of(1, 5); // 1번째 페이지, 페이지 크기 5
        Member member1 = new Member(1L, "testUser", "test1@example.com");
        Member member2 = new Member(2L, "anotherUser", "test2@example.com");

        List<Member> members = List.of(member1, member2);
        Page<Member> pagedMembers = new PageImpl<>(members, pageable, members.size());

        when(memberRepository.findByNicknameContainingIgnoreCaseOrEmailContainingIgnoreCase(searchTerm, searchTerm, pageable))
                .thenReturn(pagedMembers);

        // When
        SearchMemberListResponseDto result = memberService.searchMember(searchTerm, pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.searchMemberResponseDtoList().size());
        assertEquals("testUser", result.searchMemberResponseDtoList().get(0).nickname());
        assertEquals("test1@example.com", result.searchMemberResponseDtoList().get(0).email());
    }

    @Test
    @DisplayName("회원 검색 실패 테스트 - searchTerm이 null이거나 빈 경우")
    void testSearchMember_Failure_NullSearchTerm() {
        // Given
        String searchTerm = null;
        Pageable pageable = PageRequest.of(1, 5);

        // When
        doThrow(new SearchTermNotFoundException()).when(memberValidator).validateSearchTerm(null);

        // Then
        assertThrows(SearchTermNotFoundException.class, () -> memberService.searchMember(searchTerm,pageable));
    }






}
