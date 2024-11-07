package com.narara.superboard.attachment.service;

import com.narara.superboard.attachment.entity.Attachment;
import com.narara.superboard.attachment.infrastructure.AttachmentRepository;
import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.infrastructure.CardRepository;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.common.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AttachmentServiceImplTest {
    @Mock
    private AttachmentRepository attachmentRepository;

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private AttachmentServiceImpl attachmentService;

    private Card testCard;
    private Attachment testAttachment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testCard = Card.builder().id(1L)
                .cover(new HashMap<>(){{
                    put("type", "IMAGE");
                    put("value", "ddd.img");
                }})
                .build();
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
    void testAddAttachment_Success() {
        // Arrange
        when(cardRepository.findById(testCard.getId())).thenReturn(Optional.of(testCard));
        when(attachmentRepository.existsByCardIdAndIsDeletedFalse(testCard.getId())).thenReturn(false);
        when(attachmentRepository.save(any(Attachment.class))).thenReturn(testAttachment);

        // Act
        Attachment result = attachmentService.addAttachment(testCard.getId(), "http://example.com/image.jpg");

        // Assert
        assertNotNull(result);
        assertEquals("http://example.com/image.jpg", result.getUrl());
        assertTrue(result.getIsCover());
        verify(cardRepository, times(1)).save(testCard);
        verify(attachmentRepository, times(1)).save(any(Attachment.class));
    }

    @Test
    @DisplayName("첨부파일 추가시, 이미 첨부파일이 있는 경우 isCover = false로 등록")
    void testAddAttachment_Success2() {
        // Arrange
        when(cardRepository.findById(testCard.getId())).thenReturn(Optional.of(testCard));
        when(attachmentRepository.existsByCardIdAndIsDeletedFalse(testCard.getId())).thenReturn(true);
        when(attachmentRepository.save(any(Attachment.class))).thenReturn(testAttachment);

        // Act
        Attachment result = attachmentService.addAttachment(testCard.getId(), "http://example.com/image.jpg");

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
        when(cardRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(NotFoundEntityException.class, () ->
                attachmentService.addAttachment(999L, "http://example.com/image.jpg"));

        assertEquals("해당하는 카드(이)가 존재하지 않습니다. 카드ID: 999", exception.getMessage());
        verify(attachmentRepository, never()).save(any(Attachment.class));
    }

    @Test
    @DisplayName("첨부파일 추가 시 URL이 null이면 예외 발생")
    void testAddAttachment_Failure_NullUrl() {
        // Arrange
        when(cardRepository.findById(testCard.getId())).thenReturn(Optional.of(testCard));

        // Act & Assert
        Exception exception = assertThrows(NotFoundException.class, () ->
                attachmentService.addAttachment(testCard.getId(), null));

        assertEquals("첨부파일의 url(이)가 존재하지 않습니다. url(을)를 작성해주세요.", exception.getMessage());
        verify(attachmentRepository, never()).save(any(Attachment.class));
    }

    @Test
    @DisplayName("첨부파일 추가 시 URL이 빈 문자열이면 예외 발생")
    void testAddAttachment_Failure_EmptyUrl() {
        // Arrange
        when(cardRepository.findById(testCard.getId())).thenReturn(Optional.of(testCard));

        // Act & Assert
        Exception exception = assertThrows(NotFoundException.class, () ->
                attachmentService.addAttachment(testCard.getId(), ""));

        assertEquals("첨부파일의 url(이)가 존재하지 않습니다. url(을)를 작성해주세요.", exception.getMessage());
        verify(attachmentRepository, never()).save(any(Attachment.class));
    }

    /**
     * Tests for deleteAttachment
     */
    @Test
    @DisplayName("성공적으로 첨부파일을 삭제")
    void testDeleteAttachment_Success() {
        // Arrange
        testAttachment.setIsCover(true);
        // 커버가 이미 카드에 있는 경우

        when(attachmentRepository.findById(testAttachment.getId())).thenReturn(Optional.of(testAttachment));

        // Act
        attachmentService.deleteAttachment(testAttachment.getId());

        // Assert
        assertTrue(testAttachment.getIsDeleted());
        assertFalse(testAttachment.getIsCover());
        assertNull(testCard.getCover());
        verify(attachmentRepository, times(1)).save(testAttachment);
        verify(cardRepository, times(1)).save(testCard);
    }

    @Test
    @DisplayName("첨부파일 삭제 시 존재하지 않으면 예외 발생")
    void testDeleteAttachment_Failure_NotFound() {
        // Arrange
        when(attachmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(NotFoundEntityException.class, () ->
                attachmentService.deleteAttachment(999L));

        assertEquals("해당하는 첨부파일(이)가 존재하지 않습니다. 첨부파일ID: 999", exception.getMessage());
        verify(attachmentRepository, never()).save(any(Attachment.class));
    }

    @Test
    @DisplayName("첨부파일이 커버일 때 카드의 커버 이미지를 제거하지 못하면 예외 발생")
    void testDeleteAttachment_Failure_CardCoverUpdateError() {
        // Arrange
        testAttachment.setIsCover(true); // Mark the attachment as cover

        when(attachmentRepository.findById(testAttachment.getId())).thenReturn(Optional.of(testAttachment));
        doThrow(new RuntimeException("카드 커버 업데이트 실패")).when(cardRepository).save(testCard);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () ->
                attachmentService.deleteAttachment(testAttachment.getId()));

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

        when(attachmentRepository.findById(testAttachment.getId())).thenReturn(Optional.of(testAttachment));
        when(cardRepository.findById(testCard.getId())).thenReturn(Optional.of(testCard));

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

        when(attachmentRepository.findById(testAttachment.getId())).thenReturn(Optional.of(testAttachment));
        when(cardRepository.findById(testCard.getId())).thenReturn(Optional.of(testCard));

        // Act
        attachmentService.updateAttachmentIsCover(testAttachment.getId());

        // Assert
        assertFalse(testAttachment.getIsCover());
        assertNull(testCard.getCover());
        verify(attachmentRepository, times(1)).save(testAttachment);
        verify(cardRepository, times(1)).save(testCard);
    }

    @Test
    @DisplayName("커버 여부 업데이트 시 첨부파일이 존재하지 않으면 예외 발생")
    void testUpdateAttachmentIsCover_Failure_NotFound() {
        // Arrange
        when(attachmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(NotFoundEntityException.class, () ->
                attachmentService.updateAttachmentIsCover(999L));

        assertEquals("해당하는 첨부파일(이)가 존재하지 않습니다. 첨부파일ID: 999", exception.getMessage());
        verify(attachmentRepository, never()).save(any(Attachment.class));
    }
}