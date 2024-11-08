package com.narara.superboard.fcmtoken.service;

import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.common.exception.NotFoundException;
import com.narara.superboard.fcmtoken.entity.FcmToken;
import com.narara.superboard.fcmtoken.infrastructure.FcmTokenRepository;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.infrastructure.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

class FcmTokenServiceImplTest {
    @Mock
    private FcmTokenRepository fcmTokenRepository;

    @InjectMocks
    private FcmTokenServiceImpl fcmTokenService;

    @Mock
    private MemberRepository memberRepository;

    private Member testMember;
    private FcmToken testFcmToken;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testMember = Member.builder().id(1L).build();
        testFcmToken = FcmToken.builder()
                .id(1L)
                .member(testMember)
                .registrationToken("test_registration_token")
                .build();
    }

    /**
     * Tests for createFcmToken
     */
    @Test
    @DisplayName("성공적으로 FCM 토큰을 생성")
    void testCreateFcmToken_Success() {
        // Arrange
        when(fcmTokenRepository.save(any(FcmToken.class))).thenReturn(testFcmToken);
        when(memberRepository.findByIdAndIsDeletedFalse(any(Long.class))).thenReturn(Optional.of(testMember));

        // Act
        FcmToken result = fcmTokenService.createFcmToken(testMember.getId(), "test_registration_token");

        // Assert
        assertNotNull(result);
        assertEquals(testFcmToken.getRegistrationToken(), result.getRegistrationToken());
        verify(fcmTokenRepository, times(1)).save(any(FcmToken.class));
    }

    @Test
    @DisplayName("FCM 토큰 생성 시 memberId가 null인 경우 예외 발생")
    void testCreateFcmToken_Failure_NullMemberId() {

        when(memberRepository.findByIdAndIsDeletedFalse(any(Long.class))).thenReturn(null);

        // Act & Assert
       assertThrows(NotFoundEntityException.class, () ->
                fcmTokenService.createFcmToken(null, "test_registration_token"));

        verify(fcmTokenRepository, never()).save(any(FcmToken.class));
    }

    @Test
    @DisplayName("FCM 토큰 생성 시 registrationToken이 null인 경우 예외 발생")
    void testCreateFcmToken_Failure_NullRegistrationToken() {

        when(memberRepository.findByIdAndIsDeletedFalse(any(Long.class))).thenReturn(Optional.of(testMember));

        // Act & Assert
        Exception exception = assertThrows(NotFoundException.class, () ->
                fcmTokenService.createFcmToken(testMember.getId(), null));

        assertEquals("fcm토큰의 토큰(이)가 존재하지 않습니다. 토큰(을)를 작성해주세요.", exception.getMessage());
        verify(fcmTokenRepository, never()).save(any(FcmToken.class));
    }

    /**
     * Tests for updateFcmToken
     */
    @Test
    @DisplayName("성공적으로 FCM 토큰을 업데이트")
    void testUpdateFcmToken_Success() {
        // Arrange
        when(fcmTokenRepository.findByMemberId(testMember.getId())).thenReturn(Optional.of(testFcmToken));
        when(fcmTokenRepository.save(any(FcmToken.class))).thenReturn(testFcmToken);
        when(memberRepository.findByIdAndIsDeletedFalse(any(Long.class))).thenReturn(Optional.of(testMember));

        // Act
        FcmToken result = fcmTokenService.updateFcmToken(testMember.getId(), "updated_registration_token");

        // Assert
        assertNotNull(result);
        assertEquals("updated_registration_token", result.getRegistrationToken());
        verify(fcmTokenRepository, times(1)).findByMemberId(testMember.getId());
        verify(fcmTokenRepository, times(1)).save(any(FcmToken.class));
    }

    @Test
    @DisplayName("존재하지 않는 회원 ID로 FCM 토큰 업데이트 시 예외 발생")
    void testUpdateFcmToken_Failure_MemberNotFound() {
        // Arrange

        Long nonExistentMemberId = 999L;
        when(memberRepository.findByIdAndIsDeletedFalse(any(Long.class))).thenReturn(Optional.empty());
        when(fcmTokenRepository.findByMemberId(nonExistentMemberId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(NotFoundEntityException.class, () ->
                fcmTokenService.updateFcmToken(nonExistentMemberId, "updated_registration_token"));

        verify(fcmTokenRepository, never()).findByMemberId(nonExistentMemberId);
        verify(fcmTokenRepository, never()).delete(any(FcmToken.class));
    }

    @Test
    @DisplayName("FCM 토큰 업데이트 시 registrationToken이 null인 경우 예외 발생")
    void testUpdateFcmToken_Failure_NullRegistrationToken() {
        // Arrange
        when(memberRepository.findByIdAndIsDeletedFalse(any(Long.class))).thenReturn(Optional.of(testMember));

        // Act & Assert
        Exception exception = assertThrows(NotFoundException.class, () ->
                fcmTokenService.updateFcmToken(testMember.getId(), null));

        assertEquals("fcm토큰의 토큰(이)가 존재하지 않습니다. 토큰(을)를 작성해주세요.", exception.getMessage());
        verify(fcmTokenRepository, never()).save(any(FcmToken.class));
    }

    @Test
    @DisplayName("FCM 토큰 업데이트 시 빈 registrationToken인 경우 예외 발생")
    void testUpdateFcmToken_Failure_EmptyRegistrationToken() {
        // Arrange
        when(memberRepository.findByIdAndIsDeletedFalse(any(Long.class))).thenReturn(Optional.of(testMember));

        // Act & Assert
        Exception exception = assertThrows(NotFoundException.class, () ->
                fcmTokenService.updateFcmToken(testMember.getId(), ""));

        assertEquals("fcm토큰의 토큰(이)가 존재하지 않습니다. 토큰(을)를 작성해주세요.", exception.getMessage());
        verify(fcmTokenRepository, never()).save(any(FcmToken.class));
    }

    /**
     * Tests for deleteFcmToken
     */
    @Test
    @DisplayName("성공적으로 FCM 토큰을 삭제")
    void testDeleteFcmToken_Success() {
        // Arrange
//        when(memberRepository.findByIdAndIsDeletedFalse(any(Long.class))).thenReturn(Optional.of(testMember));
        when(fcmTokenRepository.findByMember(testMember)).thenReturn(Optional.of(testFcmToken));
        doNothing().when(fcmTokenRepository).delete(testFcmToken);

        // Act
        fcmTokenService.deleteFcmToken(testMember);

        // Assert
        verify(fcmTokenRepository, times(1)).findByMember(testMember);
        verify(fcmTokenRepository, times(1)).delete(testFcmToken);
    }

    @Disabled
    @Test
    @DisplayName("FCM 토큰 삭제 시 존재하지 않는 회원 ID로 예외 발생")
    void testDeleteFcmToken_Failure_MemberNotFound() {
        // Arrange
//        Long nonExistentMemberId = 999L;
        Member member = new Member(1L , "시현", "sisi@naver.com");
        when(memberRepository.findByIdAndIsDeletedFalse(member.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () ->
                fcmTokenService.deleteFcmToken(member));

        verify(fcmTokenRepository, never()).delete(any(FcmToken.class));
    }

    @Test
    @DisplayName("FCM 토큰 삭제 시 토큰이 없는 경우 예외 발생")
    void testDeleteFcmToken_Failure_FcmTokenNotFound() {
        // Arrange
        when(memberRepository.findByIdAndIsDeletedFalse(any(Long.class))).thenReturn(Optional.of(testMember));
        when(fcmTokenRepository.findByMemberId(testMember.getId())).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(NotFoundException.class, () ->
                fcmTokenService.deleteFcmToken(testMember));

        assertEquals("FcmToken의 토큰(이)가 존재하지 않습니다. 토큰(을)를 작성해주세요.", exception.getMessage());
        verify(fcmTokenRepository, times(1)).findByMember(testMember);
        verify(fcmTokenRepository, never()).delete(any(FcmToken.class));
    }


}
