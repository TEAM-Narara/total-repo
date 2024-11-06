package com.narara.superboard.memberbackground.service;

import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.common.exception.NotFoundException;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.exception.MemberNotFoundException;
import com.narara.superboard.member.infrastructure.MemberRepository;
import com.narara.superboard.memberbackground.entity.MemberBackground;
import com.narara.superboard.memberbackground.infrastructure.MemberBackgroundRepository;
import com.narara.superboard.memberbackground.service.validator.MemberBackgroundValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MemberBackgroundServiceImplTest {
    @Mock
    private MemberBackgroundRepository memberBackgroundRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberBackgroundValidator memberBackgroundValidator;

    @InjectMocks
    private MemberBackgroundServiceImpl memberBackgroundService;

    private Member testMember;
    private MemberBackground testBackground;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testMember = Member.builder().id(1L).build();
        testBackground = MemberBackground.builder()
                .id(1L)
                .member(testMember)
                .imgUrl("http://example.com/image.jpg")
                .build();

        // Mock member repository to return the test member
        when(memberRepository.findById(testMember.getId())).thenReturn(java.util.Optional.of(testMember));

        // Mock the validator to throw NotFoundException if imgUrl is null
        doThrow(new NotFoundException("멤버 배경의 imgUrl(이)가 존재하지 않습니다. imgUrl(을)를 작성해주세요."))
                .when(memberBackgroundValidator).validateImgUrl(null);

        doThrow(new NotFoundException("멤버 배경의 imgUrl(이)가 존재하지 않습니다. imgUrl(을)를 작성해주세요."))
                .when(memberBackgroundValidator).validateImgUrl("");
    }

    @Test
    @DisplayName("성공적으로 회원 배경을 추가")
    void testAddMemberBackground_Success() {
        // Arrange
        when(memberBackgroundRepository.save(any(MemberBackground.class))).thenReturn(testBackground);

        // Act
        MemberBackground result = memberBackgroundService.addMemberBackground(testMember.getId(), "http://example.com/image.jpg");

        // Assert
        assertNotNull(result);
        assertEquals(testBackground.getImgUrl(), result.getImgUrl());
        verify(memberBackgroundRepository, times(1)).save(any(MemberBackground.class));
    }

    @Test
    @DisplayName("회원 정보가 없는 경우 회원 배경 추가 시 예외가 발생해야 한다.")
    void testAddMemberBackground_Failure_MemberNotFound() {
        // Arrange
        Long nonExistentMemberId = 999L;

        // Simulate that the member does not exist
        when(memberRepository.findById(nonExistentMemberId)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(MemberNotFoundException.class, () ->
                memberBackgroundService.addMemberBackground(nonExistentMemberId, "http://example.com/image.jpg"));

        assertEquals("ID가 999인 회원을 찾을 수 없습니다.(이)가 존재하지 않습니다. ID가 999인 회원을 찾을 수 없습니다.(을)를 작성해주세요.", exception.getMessage());
        verify(memberBackgroundRepository, never()).save(any(MemberBackground.class));
    }

    @Test
    @DisplayName("이미지 URL이 null인 경우 NotFoundException 예외가 발생해야 한다.")
    void testAddMemberBackground_Failure_NullImgUrl() {
        // Act & Assert
        Exception exception = assertThrows(NotFoundException.class, () ->
                memberBackgroundService.addMemberBackground(testMember.getId(), null));

        assertEquals("멤버 배경의 imgUrl(이)가 존재하지 않습니다. imgUrl(을)를 작성해주세요.(이)가 존재하지 않습니다. 멤버 배경의 imgUrl(이)가 존재하지 않습니다. imgUrl(을)를 작성해주세요.(을)를 작성해주세요.", exception.getMessage());
        verify(memberBackgroundRepository, never()).save(any(MemberBackground.class));
    }

    @Test
    @DisplayName("이미지 URL이 빈 문자열인 경우 NotFoundException 예외가 발생해야 한다.")
    void testAddMemberBackground_Failure_EmptyImgUrl() {
        // Act & Assert
        Exception exception = assertThrows(NotFoundException.class, () ->
                memberBackgroundService.addMemberBackground(testMember.getId(), ""));

        assertEquals("멤버 배경의 imgUrl(이)가 존재하지 않습니다. imgUrl(을)를 작성해주세요.(이)가 존재하지 않습니다. 멤버 배경의 imgUrl(이)가 존재하지 않습니다. imgUrl(을)를 작성해주세요.(을)를 작성해주세요.", exception.getMessage());
        verify(memberBackgroundRepository, never()).save(any(MemberBackground.class));
    }

    /**
     * ------------------------------------------------------------------------------------
     */

    @Test
    @DisplayName("특정 회원의 모든 배경 리스트를 성공적으로 조회")
    void testGetMemberBackgrounds_Success() {
        // Arrange
        when(memberBackgroundRepository.findAllByMemberId(testMember.getId())).thenReturn(List.of(testBackground));

        // Act
        List<MemberBackground> backgrounds = memberBackgroundService.getAllMemberBackground(testMember.getId());

        // Assert
        assertNotNull(backgrounds);
        assertEquals(1, backgrounds.size());
        assertEquals(testBackground.getImgUrl(), backgrounds.get(0).getImgUrl());
        verify(memberBackgroundRepository, times(1)).findAllByMemberId(testMember.getId());
    }

    @Test
    @DisplayName("존재하지 않는 회원 ID로 배경 리스트 조회 시 예외가 발생해야 한다.")
    void testGetMemberBackgrounds_Failure_MemberNotFound() {
        // Arrange
        Long nonExistentMemberId = 999L;

        // Simulate that the member does not exist in memberRepository
        when(memberRepository.findById(nonExistentMemberId)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(MemberNotFoundException.class, () ->
                memberBackgroundService.getAllMemberBackground(nonExistentMemberId));

        assertEquals("ID가 999인 회원을 찾을 수 없습니다.(이)가 존재하지 않습니다. ID가 999인 회원을 찾을 수 없습니다.(을)를 작성해주세요.", exception.getMessage());
        verify(memberBackgroundRepository, never()).findAllByMemberId(nonExistentMemberId);
        verify(memberRepository, times(1)).findById(nonExistentMemberId);
    }

    @Test
    @DisplayName("회원 ID에 해당하는 배경이 없을 때 빈 리스트를 반환해야 한다.")
    void testGetMemberBackgrounds_Success_EmptyList() {
        // Arrange
        when(memberBackgroundRepository.findAllByMemberId(testMember.getId())).thenReturn(Collections.emptyList());

        // Act
        List<MemberBackground> backgrounds = memberBackgroundService.getAllMemberBackground(testMember.getId());

        // Assert
        assertNotNull(backgrounds); // Ensure the list is not null
        assertTrue(backgrounds.isEmpty()); // Check that the list is empty
        verify(memberBackgroundRepository, times(1)).findAllByMemberId(testMember.getId());
    }

    @Test
    @DisplayName("존재하지 생않는 ID로 회원 배경을 삭제하려고 할 때 예외가 발")
    void testDeleteMemberBackground_Failure_NotFound() {
        // Arrange
        Long nonExistentMemberId = 999L;
        Long backgroundId = 123L;

        when(memberRepository.findById(nonExistentMemberId)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(MemberNotFoundException.class, () ->
                memberBackgroundService.deleteMemberBackground(nonExistentMemberId,backgroundId));

        assertEquals("ID가 999인 회원을 찾을 수 없습니다.(이)가 존재하지 않습니다. ID가 999인 회원을 찾을 수 없습니다.(을)를 작성해주세요.", exception.getMessage());

        verify(memberBackgroundRepository, never()).findByIdAndMemberId(backgroundId,nonExistentMemberId);
        verify(memberBackgroundRepository, never()).deleteById(backgroundId);
    }

    @Test
    @DisplayName("배경 ID가 null인 경우 삭제 시 예외가 발생해야 한다.")
    void testDeleteMemberBackground_Failure_NullBackgroundId() {
        // Act & Assert
        Exception exception = assertThrows(NotFoundEntityException.class, () ->
                memberBackgroundService.deleteMemberBackground(testMember.getId(), null));

        assertEquals("해당하는 멤버 배경(이)가 존재하지 않습니다. 멤버 배경ID: null", exception.getMessage());
        verify(memberBackgroundRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("회원 ID와 배경 ID가 일치하지 않는 경우 예외가 발생해야 한다.")
    void testDeleteMemberBackground_Failure_MismatchedMemberAndBackground() {
        // Arrange
        Long backgroundId = testBackground.getId();
        when(memberBackgroundRepository.findByIdAndMemberId(backgroundId, testMember.getId())).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(NotFoundEntityException.class, () ->
                memberBackgroundService.deleteMemberBackground(testMember.getId(), backgroundId));

        assertEquals("해당하는 멤버 배경(이)가 존재하지 않습니다. 멤버 배경ID: " + backgroundId, exception.getMessage());
        verify(memberBackgroundRepository, times(1)).findByIdAndMemberId(backgroundId, testMember.getId());
        verify(memberBackgroundRepository, never()).deleteById(backgroundId);
    }
}