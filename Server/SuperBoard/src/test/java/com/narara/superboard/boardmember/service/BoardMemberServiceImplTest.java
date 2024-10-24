package com.narara.superboard.boardmember.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.narara.superboard.board.service.validator.BoardValidator;
import com.narara.superboard.boardmember.entity.BoardMember;
import com.narara.superboard.boardmember.infrastructure.BoardMemberRepository;
import com.narara.superboard.boardmember.interfaces.dto.BoardMemberResponseDto;
import com.narara.superboard.common.constant.enums.Authority;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.boardmember.interfaces.dto.BoardMemberCollectionResponseDto;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("BoardMemberServiceImpl 대한 Test")
class BoardMemberServiceImplTest {

    @InjectMocks
    private BoardMemberServiceImpl boardMemberService;

    @Mock
    private BoardMemberRepository boardMemberRepository;
    @Mock
    private BoardValidator boardValidator;

    @BeforeEach
    void setUp() {
        // Mockito가 Mock 객체를 초기화
        MockitoAnnotations.openMocks(this);
    }


    @Test
    @DisplayName("워크스페이스에 권한이 있는 멤버 리스트 조회 성공 테스트")
    void testGetBoardMemberCollectionResponseDtoSuccess() {
        // given
        Long boardId = 1L;

        // Mock된 Member와 BoardMember 데이터 설정
        Member mockMember1 = new Member(1L, "닉네임1", "user1@example.com", "http://profile1.img");
        Member mockMember2 = new Member(2L, "닉네임2", "user2@example.com", "http://profile2.img");


        BoardMember mockBoardMember1 = new BoardMember(mockMember1, Authority.ADMIN);
        BoardMember mockBoardMember2 = new BoardMember(mockMember2, Authority.MEMBER);

        List<BoardMember> mockBoardMemberList = List.of(mockBoardMember1, mockBoardMember2);

        // when: boardMemberRepository의 동작을 정의
        when(boardMemberRepository.findAllByBoardId(boardId)).thenReturn(mockBoardMemberList);

        // when: 서비스 메서드 호출
        BoardMemberCollectionResponseDto result = boardMemberService.getBoardMemberCollectionResponseDto(boardId);

        // then: 결과 검증
        assertEquals(2, result.boardMemberResponseDtoList().size());

        // 첫 번째 멤버에 대한 검증
        BoardMemberResponseDto firstMember = result.boardMemberResponseDtoList().get(0);
        assertEquals(1L, firstMember.memberId());
        assertEquals("user1@example.com", firstMember.memberEmail());
        assertEquals("닉네임1", firstMember.memberNickname());
        assertEquals("http://profile1.img", firstMember.memberProfileImgUrl());
        assertEquals("ADMIN", firstMember.authority());

        // 두 번째 멤버에 대한 검증
        BoardMemberResponseDto secondMember = result.boardMemberResponseDtoList().get(1);
        assertEquals(2L, secondMember.memberId());
        assertEquals("user2@example.com", secondMember.memberEmail());
        assertEquals("닉네임2", secondMember.memberNickname());
        assertEquals("http://profile2.img", secondMember.memberProfileImgUrl());
        assertEquals("MEMBER", secondMember.authority());

        // verify: boardMemberRepository가 정확히 한 번 호출되었는지 확인
        verify(boardMemberRepository, times(1)).findAllByBoardId(boardId);
    }


}
