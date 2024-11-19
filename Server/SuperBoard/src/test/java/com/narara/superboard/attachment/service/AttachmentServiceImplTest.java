package com.narara.superboard.attachment.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.narara.superboard.MockSuperBoardUnitTests;
import com.narara.superboard.attachment.entity.Attachment;
import com.narara.superboard.attachment.infrastructure.AttachmentRepository;
import com.narara.superboard.board.entity.Board;
import com.narara.superboard.board.service.kafka.BoardOffsetService;
import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.infrastructure.CardHistoryRepository;
import com.narara.superboard.card.infrastructure.CardRepository;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.common.exception.NotFoundException;
import com.narara.superboard.fcmtoken.service.AlarmService;
import com.narara.superboard.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AttachmentServiceImplTest implements MockSuperBoardUnitTests {
    @Mock
    private AttachmentRepository attachmentRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardHistoryRepository cardHistoryRepository;

    @Mock
    private BoardOffsetService boardOffsetService;

    @Mock
    private AlarmService alarmService;

    @InjectMocks
    private AttachmentServiceImpl attachmentService;

    private Card testCard;
    private Attachment testAttachment;

    @BeforeEach
    void setUp() {
        // Board 객체 생성
        Board testBoard = Board.builder().id(1L).name("Test Board").build();

        // List 객체 생성 및 Board 설정
        com.narara.superboard.list.entity.List testList = com.narara.superboard.list.entity.List.builder()
                .id(10001L)
                .name("Test List")
                .board(testBoard)
                .build();

        // Card 객체 생성 및 List 설정
        testCard = Card.builder().id(1L)
                .list(testList)  // Card에 List 추가
                .cover(new HashMap<>(){{
                    put("type", "IMAGE");
                    put("value", "ddd.img");
                }})
                .build();

        // Attachment 객체 생성
        testAttachment = Attachment.builder()
                .id(1L)
                .card(testCard)
                .url("http://example.com/image.jpg")
                .isCover(false)
                .isDeleted(false)
                .build();
    }


    /**
     * Tests for addAttachment
     */
    @Test
    @DisplayName("성공적으로 첨부파일을 추가")
    void testAddAttachment_Success() throws FirebaseMessagingException {
        Member member = new Member(1L, "시현", "sisi@naver.com");

        // Arrange
        when(cardRepository.findByIdAndIsDeletedFalse(testCard.getId())).thenReturn(Optional.of(testCard));
        when(attachmentRepository.existsByCardIdAndIsDeletedFalse(testCard.getId())).thenReturn(false);
        when(attachmentRepository.save(any(Attachment.class))).thenReturn(testAttachment);

        // Act
        Attachment result = attachmentService.addAttachment(member, testCard.getId(), "http://example.com/image.jpg");

        // Assert
        assertNotNull(result);
        assertEquals("http://example.com/image.jpg", result.getUrl());
        assertTrue(result.getIsCover());
        verify(cardRepository, times(1)).save(testCard);
        verify(attachmentRepository, times(1)).save(any(Attachment.class));
    }

    @Test
    @DisplayName("첨부파일 추가시, 이미 첨부파일이 있는 경우 isCover = false로 등록")
    void testAddAttachment_Success2() throws FirebaseMessagingException {
        // Arrange
        when(cardRepository.findByIdAndIsDeletedFalse(testCard.getId())).thenReturn(Optional.of(testCard));
        when(attachmentRepository.existsByCardIdAndIsDeletedFalse(testCard.getId())).thenReturn(true);
        when(attachmentRepository.save(any(Attachment.class))).thenReturn(testAttachment);
        Member member = new Member(1L, "시현", "sisi@naver.com");

        // Act
        Attachment result = attachmentService.addAttachment(member, testCard.getId(), "http://example.com/image.jpg");

        // Assert
        assertNotNull(result);
        assertEquals("http://example.com/image.jpg", result.getUrl());
        assertFalse(result.getIsCover());
        verify(cardRepository, never()).save(testCard);
        verify(attachmentRepository, times(1)).save(any(Attachment.class));
    }

    @Test
    @DisplayName("첨부파일 추가 시 카드가 존재하지 않으면 예외 발생")
    void testAddAttachment_Failure_CardNotFound() {
        // Arrange
//        when(cardRepository.findByIdAndIsDeletedFalse(anyLong())).thenReturn(Optional.empty());
        Member member = new Member(1L, "시현", "sisi@naver.com");

        // Act & Assert
        Exception exception = assertThrows(NotFoundEntityException.class, () ->
                attachmentService.addAttachment(member, 999L, "http://example.com/image.jpg"));

        assertEquals("해당하는 카드(이)가 존재하지 않습니다. 카드ID: 999", exception.getMessage());
        verify(attachmentRepository, never()).save(any(Attachment.class));
    }

    @Test
    @DisplayName("첨부파일 추가 시 URL이 null이면 예외 발생")
    void testAddAttachment_Failure_NullUrl() {
        // Arrange
//        when(cardRepository.findByIdAndIsDeletedFalse(testCard.getId())).thenReturn(Optional.of(testCard));
        Member member = new Member(1L, "시현", "sisi@naver.com");

        // Act & Assert
        Exception exception = assertThrows(NotFoundException.class, () ->
                attachmentService.addAttachment(member, testCard.getId(), null));

        assertEquals("첨부파일의 url(이)가 존재하지 않습니다. url(을)를 작성해주세요.", exception.getMessage());
        verify(attachmentRepository, never()).save(any(Attachment.class));
    }

    /**
     * 이 UnnecessaryStubbingException 오류는 불필요한 목 설정이 포함되어 있음을 나타냅니다.
     * testAddAttachment_Failure_EmptyUrl 테스트 메서드에서 cardRepository.findByIdAndIsDeletedFalse(testCard.getId())를 스텁(stub)했지만, 이 테스트에서는 실제로 그 호출이 사용되지 않아 Mockito가 불필요한 스텁으로 인식하고 오류를 발생시킵니다.
     *
     * 원인 분석
     * testAddAttachment_Failure_EmptyUrl 테스트의 목적은 첨부파일 URL이 빈 문자열일 때 예외가 발생하는지 확인하는 것입니다. 따라서, attachmentService.addAttachment 메서드 호출 시 URL의 유효성 검사를 통과하지 못하고 예외가 발생하여, cardRepository.findByIdAndIsDeletedFalse(testCard.getId())는 실제로 호출되지 않습니다.
     *
     * 해결 방법
     * 불필요한 스텁을 제거하거나,
     * lenient()를 사용해 스텁을 필요할 때만 유연하게 허용합니다.
     */
    @Test
    @DisplayName("첨부파일 추가 시 URL이 빈 문자열이면 예외 발생")
    void testAddAttachment_Failure_EmptyUrl() {
        // Arrange
        Member member = new Member(1L, "시현", "sisi@naver.com");

        // Act & Assert
        Exception exception = assertThrows(NotFoundException.class, () ->
                attachmentService.addAttachment(member, testCard.getId(), ""));

        assertEquals("첨부파일의 url(이)가 존재하지 않습니다. url(을)를 작성해주세요.", exception.getMessage());
        verify(attachmentRepository, never()).save(any(Attachment.class));
        verify(cardRepository, never()).findById(0L);
    }

    /**
     * Tests for deleteAttachment
     */
    @Test
    @DisplayName("성공적으로 첨부파일을 삭제")
    void testDeleteAttachment_Success() {
        Member member = new Member(1L, "시현", "sisi@naver.com");

        // Arrange
        testAttachment.setIsCover(true);
        // 커버가 이미 카드에 있는 경우

        when(attachmentRepository.findByIdAndIsDeletedFalse(testAttachment.getId())).thenReturn(Optional.of(testAttachment));

        // Act
        attachmentService.deleteAttachment(member, testAttachment.getId());

        // Assert
        assertTrue(testAttachment.getIsDeleted());
        assertFalse(testAttachment.getIsCover());
        verify(attachmentRepository, times(1)).save(testAttachment);
        verify(cardRepository, times(1)).save(testCard);
    }

    @Test
    @DisplayName("첨부파일 삭제 시 존재하지 않으면 예외 발생")
    void testDeleteAttachment_Failure_NotFound() {
        Member member = new Member(1L, "시현", "sisi@naver.com");

        // Arrange
        when(attachmentRepository.findByIdAndIsDeletedFalse(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(NotFoundEntityException.class, () ->
                attachmentService.deleteAttachment(member, 999L));

        assertEquals("해당하는 첨부파일(이)가 존재하지 않습니다. 첨부파일ID: 999", exception.getMessage());
        verify(attachmentRepository, never()).save(any(Attachment.class));
    }

    @Test
    @DisplayName("첨부파일이 커버일 때 카드의 커버 이미지를 제거하지 못하면 예외 발생")
    void testDeleteAttachment_Failure_CardCoverUpdateError() {
        Member member = new Member(1L, "시현", "sisi@naver.com");

        // Arrange
        testAttachment.setIsCover(true); // Mark the attachment as cover

        when(attachmentRepository.findByIdAndIsDeletedFalse(testAttachment.getId())).thenReturn(Optional.of(testAttachment));
        doThrow(new RuntimeException("카드 커버 업데이트 실패")).when(cardRepository).save(testCard);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () ->
                attachmentService.deleteAttachment(member, testAttachment.getId()));

        assertEquals("카드 커버 업데이트 실패", exception.getMessage());
        verify(attachmentRepository, never()).save(testAttachment); // Ensure attachment is not saved if card update fails
    }

    /**
     * Tests for updateAttachmentIsCover
     */
    @Test
    @DisplayName("첨부파일 커버 여부를 토글하여 업데이트 - 커버 상태가 false에서 true로 변경")
    void testUpdateAttachmentIsCover_ToggleOn() {
        // Arrange
        testAttachment.setIsCover(false); // Initially not set as cover
        testCard.setCover(null);

        when(attachmentRepository.findByIdAndIsDeletedFalse(testAttachment.getId())).thenReturn(Optional.of(testAttachment));
//        when(cardRepository.findByIdAndIsDeletedFalse(testCard.getId())).thenReturn(Optional.of(testCard));

        // Act
        attachmentService.updateAttachmentIsCover(testAttachment.getId());

        // Assert
        assertTrue(testAttachment.getIsCover());
        Map<String, String> expectedCover = new HashMap<>();
        expectedCover.put("type", testAttachment.getType());
        expectedCover.put("value", testAttachment.getUrl());

        assertEquals(expectedCover, testCard.getCover());
        verify(attachmentRepository, times(1)).save(testAttachment);
        verify(cardRepository, times(1)).save(testCard);
    }

    @Test
    @DisplayName("첨부파일 커버 여부를 토글하여 업데이트 - 커버 상태가 true에서 false로 변경")
    void testUpdateAttachmentIsCover_ToggleOff() {
        // Arrange
        testAttachment.setIsCover(true); // Initially set as cover
        testCard.setCover(Map.of(testAttachment.getType(), testAttachment.getUrl())); // Set cover

        when(attachmentRepository.findByIdAndIsDeletedFalse(testAttachment.getId())).thenReturn(Optional.of(testAttachment));
//        when(cardRepository.findByIdAndIsDeletedFalse(testCard.getId())).thenReturn(Optional.of(testCard));

        // Act
        attachmentService.updateAttachmentIsCover(testAttachment.getId());

        // Assert
        assertFalse(testAttachment.getIsCover());
        assertEquals(testCard.getCover().get("type"), "NONE");
        verify(attachmentRepository, times(1)).save(testAttachment);
        verify(cardRepository, times(1)).save(testCard);
    }

    @Test
    @DisplayName("커버 여부 업데이트 시 첨부파일이 존재하지 않으면 예외 발생")
    void testUpdateAttachmentIsCover_Failure_NotFound() {
        // Arrange
        when(attachmentRepository.findByIdAndIsDeletedFalse(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(NotFoundEntityException.class, () ->
                attachmentService.updateAttachmentIsCover(999L));

        assertEquals("해당하는 첨부파일(이)가 존재하지 않습니다. 첨부파일ID: 999", exception.getMessage());
        verify(attachmentRepository, never()).save(any(Attachment.class));
    }
}
