package com.narara.integration.superboard.reply.service;

import com.narara.integration.IntegrationTest;
import com.narara.superboard.board.entity.Board;
import com.narara.superboard.board.infrastructure.BoardRepository;
import com.narara.superboard.board.interfaces.dto.BoardCreateRequestDto;
import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.infrastructure.CardRepository;
import com.narara.superboard.card.interfaces.dto.CardCreateRequestDto;
import com.narara.superboard.common.application.validator.ContentValidator;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.list.entity.List;
import com.narara.superboard.list.infrastructure.ListRepository;
import com.narara.superboard.list.interfaces.dto.ListCreateRequestDto;
import com.narara.superboard.reply.entity.Reply;
import com.narara.superboard.reply.infrastructure.ReplyRepository;
import com.narara.superboard.reply.interfaces.dto.ReplyCreateRequestDto;
import com.narara.superboard.reply.service.ReplyServiceImpl;
import com.narara.superboard.workspace.entity.WorkSpace;
import com.narara.superboard.workspace.infrastructure.WorkSpaceRepository;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceCreateRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


// @DataJpaTest @DataJpaTest는 JPA 관련된 컴포넌트만 로드하여 단위 테스트와 유사하게 JPA 레이어만 테스트하는 용도
@DisplayName("댓글 서비스와 댓글 저장소 통합 테스트")
public class ReplyServiceImplTest extends IntegrationTest {

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

    @Test
//    @DisplayName("연관된 엔티티 설정 및 댓글 생성 요청 테스트")
//    void createEntitiesAndSaveReply() {
    @DisplayName("댓글 생성 요청시 정상적으로 저장되는지 테스트")
    void createReply_Success() {
        // 1. WorkSpace 생성 및 저장
        WorkSpace workSpace = WorkSpace.createWorkSpace(new WorkSpaceCreateRequestDto("워크스페이스 이름"));
        WorkSpace savedWorkSpace = workSpaceRepository.save(workSpace);


        // 2. Board 생성, WorkSpace와 연관 설정 후 저장
        BoardCreateRequestDto boardRequest = new BoardCreateRequestDto(
                savedWorkSpace.getId(),
                "보드 이름",
                "WORKSPACE",
                Map.of("type", "IMAGE", "value","https://...")
        );
        Board board = Board.createBoard(boardRequest, savedWorkSpace);
        Board savedBoard = boardRepository.save(board);

        // 3. List 생성, Board와 연관 설정 후 저장
        ListCreateRequestDto listRequest = new ListCreateRequestDto(savedBoard.getId(), "리스트 이름");
        List list = List.createList(listRequest, savedBoard);
        List savedList = listRepository.save(list);

        // Card 생성 요청 DTO 생성
        CardCreateRequestDto cardRequest = new CardCreateRequestDto(savedList.getId(), "카드 이름");

        // when - Card 생성 및 저장
        Card card = Card.createCard(cardRequest, savedList);
        Card savedCard = cardRepository.save(card);


        String replyContent = "테스트 댓글 내용";
        ReplyCreateRequestDto requestDto = new ReplyCreateRequestDto(savedCard.getId(), replyContent);

        // when
        Reply createdReply = replyService.createReply(requestDto);

        // then
        // 실제 ContentValidator 동작 확인
        contentValidator.validateReplyContentIsEmpty(requestDto);

//        assertThat(savedCard).isNotNull();
//        assertThat(savedCard.getName()).isEqualTo("카드 이름");
//        assertThat(savedCard.getList().getId()).isEqualTo(savedList.getId());
//        assertThat(savedCard.getMyOrder()).isEqualTo(savedList.getLastCardOrder() + 1);

        assertThat(createdReply).isNotNull();
        assertThat(createdReply.getContent()).isEqualTo(replyContent);
        assertThat(createdReply.getCard().getId()).isEqualTo(savedCard.getId());

        // verify persistence
        Reply foundReply = replyRepository.findById(createdReply.getId()).orElseThrow();
        assertThat(foundReply).isNotNull();
        assertThat(foundReply.getContent()).isEqualTo(replyContent);
        assertThat(foundReply.getCard().getId()).isEqualTo(savedCard.getId());
    }

}
