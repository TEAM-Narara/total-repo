package com.narara.integration.superboard.reply.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.narara.integration.IntegrationTest;
import com.narara.superboard.board.entity.Board;
import com.narara.superboard.board.infrastructure.BoardRepository;
import com.narara.superboard.common.interfaces.dto.CoverDto;
import com.narara.superboard.board.interfaces.dto.BoardCreateRequestDto;
import com.narara.superboard.boardmember.entity.BoardMember;
import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.infrastructure.CardRepository;
import com.narara.superboard.card.interfaces.dto.CardCreateRequestDto;
import com.narara.superboard.common.application.validator.ContentValidator;
import com.narara.superboard.common.constant.enums.Authority;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.list.entity.List;
import com.narara.superboard.list.infrastructure.ListRepository;
import com.narara.superboard.list.interfaces.dto.ListCreateRequestDto;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.infrastructure.MemberRepository;
import com.narara.superboard.reply.entity.Reply;
import com.narara.superboard.reply.infrastructure.ReplyRepository;
import com.narara.superboard.reply.interfaces.dto.ReplyCreateRequestDto;
import com.narara.superboard.reply.service.ReplyServiceImpl;
import com.narara.superboard.workspace.entity.WorkSpace;
import com.narara.superboard.workspace.infrastructure.WorkSpaceRepository;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceCreateRequestDto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

//
// ReplyServiceJpaTest
//@Transactional
// 수정 시 사용
// @DataJpaTest @DataJpaTest는 JPA 관련된 컴포넌트만 로드하여 단위 테스트와 유사하게 JPA 레이어만 테스트하는 용도
@Disabled
@DisplayName("댓글 서비스와 댓글 저장소 통합 테스트")
class ReplyServiceImplTest extends IntegrationTest {

    @Autowired
    private ReplyServiceImpl replyService;

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private ContentValidator contentValidator;

    @Autowired
    private CardRepository cardRepository; // 실제 CardRepository를 사용

    @Autowired
    private ListRepository listRepository; // 실제 ListRepository 사용

    @Autowired
    private BoardRepository boardRepository; // 실제 ListRepository 사용

    @Autowired
    private WorkSpaceRepository workSpaceRepository; // 실제 ListRepository 사용

    @Autowired
    private MemberRepository memberRepository; // 실제 ListRepository 사용


    @Disabled
    @Test
    @DisplayName("댓글 생성 요청시 정상적으로 저장되는지 테스트")
    void createReply_Success() throws FirebaseMessagingException {
        // WorkSpace 생성 및 저장
        WorkSpace workSpace = WorkSpace.createWorkSpace(new WorkSpaceCreateRequestDto("워크스페이스 이름"));
        WorkSpace savedWorkSpace = workSpaceRepository.save(workSpace);

        // Board 생성 및 저장
        Map<String, Object> background = Map.of("type", "IMAGE", "value", "https://...");
        BoardCreateRequestDto boardRequest = new BoardCreateRequestDto(
                savedWorkSpace.getId(),
                "보드 이름",
                "WORKSPACE",
                new CoverDto((String)background.get("type"), (String)background.get("value")),
                false
        );
        Board board = Board.createBoard(boardRequest, savedWorkSpace);
        boardRepository.save(board);

        // BoardMember 추가 (Member 생성 후 추가)
        Member member = new Member(1L, "시현", "sisi@naver.com");
        memberRepository.save(member);
        board.getBoardMemberList().add(new BoardMember(member, Authority.ADMIN)); // boardMemberList에 추가

        // List 생성 및 저장
        ListCreateRequestDto listRequest = new ListCreateRequestDto(board.getId(), "리스트 이름");
        List list = List.createList(listRequest, board);
        listRepository.save(list);

        // Card 생성 및 저장
        CardCreateRequestDto cardRequest = new CardCreateRequestDto(list.getId(), "카드 이름");
        Card card = Card.createCard(cardRequest, list);
        Card savedCard = cardRepository.save(card);

        // Reply 생성
        String replyContent = "테스트 댓글 내용";
        ReplyCreateRequestDto requestDto = new ReplyCreateRequestDto(savedCard.getId(), replyContent);
        Reply createdReply = replyService.createReply(member, requestDto);

        // 검증
        assertThat(createdReply).isNotNull();
        assertThat(createdReply.getContent()).isEqualTo(replyContent);
        assertThat(createdReply.getCard().getId()).isEqualTo(savedCard.getId());
    }

    @Test
    @DisplayName("존재하지 않는 Reply 조회 시 NotFoundEntityException 예외 발생 테스트")
    void testGetReply_NotFound() {
        // given
        Long nonExistentReplyId = 999L;

        // when & then
        NotFoundEntityException exception = assertThrows(NotFoundEntityException.class, () -> {
            replyService.getReply(nonExistentReplyId);
        });

        // 예외 메시지가 예상한 형식으로 반환되는지 확인
        assertThat(exception.getMessage()).contains("해당하는 댓글(이)가 존재하지 않습니다. 댓글ID: " + nonExistentReplyId);


        // 예외의 원인으로 정확한 클래스 타입인지 검증 (Optional.orElseThrow()에서 발생 확인)
        assertThat(exception).isInstanceOf(NotFoundEntityException.class)
                .hasMessageContaining("ID: " + nonExistentReplyId);
    }


}
