package com.narara.superboard.board.service;

import com.narara.superboard.board.document.BoardHistory;
import com.narara.superboard.board.entity.Board;
import com.narara.superboard.board.exception.BoardNotFoundException;
import com.narara.superboard.board.infrastructure.BoardHistoryRepository;
import com.narara.superboard.board.infrastructure.BoardRepository;
import com.narara.superboard.board.infrastructure.BoardSearchRepository;
import com.narara.superboard.board.interfaces.dto.*;
import com.narara.superboard.board.interfaces.dto.log.ArchiveStatusChangeInfo;
import com.narara.superboard.board.interfaces.dto.log.CreateBoardInfo;
import com.narara.superboard.board.interfaces.dto.log.DeleteBoardInfo;
import com.narara.superboard.board.interfaces.dto.log.UpdateBoardInfo;
import com.narara.superboard.board.service.validator.BoardValidator;
import com.narara.superboard.boardmember.entity.BoardMember;
import com.narara.superboard.boardmember.infrastructure.BoardMemberRepository;
import com.narara.superboard.card.document.CardHistory;
import com.narara.superboard.card.infrastructure.CardHistoryRepository;
import com.narara.superboard.common.application.handler.CoverHandler;
import com.narara.superboard.common.application.validator.CoverValidator;
import com.narara.superboard.common.constant.enums.Authority;
import com.narara.superboard.common.constant.enums.EventData;
import com.narara.superboard.common.constant.enums.EventType;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.common.exception.authority.UnauthorizedException;
import com.narara.superboard.common.interfaces.dto.CoverDto;
import com.narara.superboard.common.interfaces.log.BoardActivityDetailResponseDto;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.exception.MemberNotFoundException;
import com.narara.superboard.member.infrastructure.MemberRepository;
import com.narara.superboard.reply.entity.Reply;
import com.narara.superboard.reply.infrastructure.ReplyRepository;
import com.narara.superboard.websocket.constant.Action;
import com.narara.superboard.workspace.entity.WorkSpace;
import com.narara.superboard.workspace.infrastructure.WorkSpaceRepository;
import com.narara.superboard.workspace.interfaces.dto.MyBoardCollectionResponse;
import com.narara.superboard.workspace.interfaces.dto.MyBoardCollectionResponse.MyBoardWorkspaceCollectionDto;
import com.narara.superboard.workspace.service.kafka.WorkspaceOffsetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final WorkSpaceRepository workspaceRepository;
    private final BoardMemberRepository boardMemberRepository;
    private final WorkspaceOffsetService workspaceOffsetService;
    private final BoardHistoryRepository boardHistoryRepository;
    private final CardHistoryRepository cardHistoryRepository;

    private final BoardValidator boardValidator;
    private final CoverValidator coverValidator;

    private final CoverHandler coverHandler;
    private final MemberRepository memberRepository;

    private final ReplyRepository replyRepository;
    private final BoardSearchRepository boardSearchRepository;

    @Override
    public List<BoardDetailResponseDto> getBoardCollectionResponseDto(Long workspaceId) {
        List<Board> boardList = boardRepository.findAllByWorkSpaceId(workspaceId);
        if (boardList.isEmpty()) return new ArrayList<>();
        List<BoardDetailResponseDto> boardDetailResponseDtoList = new ArrayList<>();

        for (Board board : boardList) {
            BoardDetailResponseDto boardDto = BoardDetailResponseDto.builder()
                    .id(board.getId())
                    .name(board.getName())
                    .cover(new CoverDto(coverHandler.getTypeValue(board.getCover()), coverHandler.getValue(board.getCover())))
                    .build();

            boardDetailResponseDtoList.add(boardDto);
        }

        return boardDetailResponseDtoList;
    }

    @Override
    public Board createBoard(Long memberId, BoardCreateRequestDto boardCreateRequestDto) {
        boardValidator.validateNameIsPresent(boardCreateRequestDto);
        boardValidator.validateVisibilityIsValid(boardCreateRequestDto);
        boardValidator.validateVisibilityIsPresent(boardCreateRequestDto);
        boardValidator.validateBackgroundIsValid(boardCreateRequestDto);

        Member member = memberRepository.findByIdAndIsDeletedFalse(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        WorkSpace workSpace = workspaceRepository.findByIdAndIsDeletedFalse(boardCreateRequestDto.workspaceId())
                .orElseThrow(() -> new NotFoundEntityException(boardCreateRequestDto.workspaceId(), "워크스페이스"));

        Board board = Board.createBoard(boardCreateRequestDto, workSpace);

        Board saveBoard = boardRepository.save(board);
        BoardMember boardMemberByAdmin = BoardMember.createBoardMemberByAdmin(saveBoard, member);
        boardMemberRepository.save(boardMemberByAdmin);

        //보드 추가의 경우, workspace 구독 시 정보를 받을 수 있다
        board.getWorkSpace().addOffset(); //workspace offset++
        workspaceOffsetService.saveAddBoardDiff(board);

        // Board 생성 로그 기록
        CreateBoardInfo createBoardInfo = new CreateBoardInfo(board.getId(), board.getName(), workSpace.getName());

        BoardHistory<CreateBoardInfo> boardHistory = BoardHistory.createBoardHistory(
                member, LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toEpochSecond(), board, EventType.CREATE, EventData.BOARD, createBoardInfo);

        boardHistoryRepository.save(boardHistory);


        return saveBoard;
    }

    @Override
    public Board getBoard(Long boardId) {
        return boardRepository.findByIdAndIsDeletedFalse(boardId)
                .orElseThrow(() -> new NotFoundEntityException(boardId, "Board"));
    }

    @Override
    public void deleteBoard(Member member, Long boardId) {
        Board board = getBoard(boardId);
        board.deleted();

        //보드 삭제(닫기)의 경우, workspace 구독 시 정보를 받을 수 있다
        board.getWorkSpace().addOffset();
        workspaceOffsetService.saveDeleteBoardDiff(board);

        // Board 삭제 로그 기록
        DeleteBoardInfo deleteBoardInfo = new DeleteBoardInfo(board.getId(), board.getName(), board.getWorkSpace().getName());

        BoardHistory<DeleteBoardInfo> boardHistory = BoardHistory.createBoardHistory(
                member, LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toEpochSecond(), board, EventType.DELETE, EventData.BOARD, deleteBoardInfo);

        boardHistoryRepository.save(boardHistory);

    }

    @Override
    public Board updateBoard(Long memberId, Long boardId, BoardUpdateRequestDto boardUpdateRequestDto) {
        boardValidator.validateVisibilityIsPresent(boardUpdateRequestDto);
        boardValidator.validateVisibilityIsValid(boardUpdateRequestDto);
        boardValidator.validateBackgroundIsValid(boardUpdateRequestDto);
        coverValidator.validateCoverTypeIsValid(boardUpdateRequestDto.cover());
        BoardMember boardMember = boardMemberRepository.findFirstByBoard_IdAndMember_Id(boardId, memberId)
                .orElseThrow(() -> new AccessDeniedException("보드에 대한 권한이 없습니다"));

        if (boardMember.getAuthority() == null) {
            throw new AccessDeniedException("보드에 대한 권한이 없습니다");
        }

        Board board = getBoard(boardId);
        Board updatedBoard;

        if (boardMember.getAuthority().equals(Authority.MEMBER)) {
            updatedBoard = board.updateBoardByMember(boardUpdateRequestDto);
        } else if (boardMember.getAuthority().equals(Authority.ADMIN)) {
            updatedBoard = board.updateBoardByAdmin(boardUpdateRequestDto);
        } else {
            throw new AccessDeniedException("보드에 대한 권한이 잘못되었습니다.");
        }

        // Board 업데이트 로그 기록
        UpdateBoardInfo updateBoardInfo = new UpdateBoardInfo(updatedBoard.getId(), updatedBoard.getName(), updatedBoard.getWorkSpace().getName());

        BoardHistory<UpdateBoardInfo> boardHistory = BoardHistory.createBoardHistory(
                memberRepository.findByIdAndIsDeletedFalse(memberId).orElseThrow(() -> new NotFoundEntityException(memberId, "멤버")),
                LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toEpochSecond(),
                updatedBoard, EventType.UPDATE, EventData.BOARD, updateBoardInfo);

        boardHistoryRepository.save(boardHistory);

        return updatedBoard;
    }

    // 아카이브된 보드 리스트 조회
    @Override
    public List<Board> getArchivedBoards(Long workspaceId) {
        return boardRepository.findAllByWorkSpaceIdAndIsArchivedTrueAndIsDeletedFalse(workspaceId);
    }

    // 보드 아카이브 상태 변경
    @Override
    public void changeArchiveStatus(Member member, Long boardId) {
        Board board = getBoard(boardId);
        board.changeArchiveStatus();

        // 아카이브 상태 변경 로그 기록
        ArchiveStatusChangeInfo archiveStatusChangeInfo = new ArchiveStatusChangeInfo(board.getId(), board.getName(), board.getIsArchived());

        BoardHistory<ArchiveStatusChangeInfo> boardHistory = BoardHistory.createBoardHistory(
                member, LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toEpochSecond(), board, EventType.CLOSE, EventData.BOARD, archiveStatusChangeInfo);

        boardHistoryRepository.save(boardHistory);
    }

    @Override
    public void checkBoardMember(Board board, Member member, Action action) {
        java.util.List<BoardMember> boardMemberList = board.getBoardMemberList();
        for (BoardMember boardMember : boardMemberList) {
            if (boardMember.getMember().getId().equals(member.getId())) {
                return;
            }
        }
        throw new UnauthorizedException(member.getNickname(), action);
    }

    @Override
    public MyBoardCollectionResponse getMyBoardList(Long memberId, String keyword) {
        MyBoardCollectionResponse myBoardCollectionResponse;

        if (keyword == null) {
            List<BoardMember> boardMemberList = boardMemberRepository.findByMemberId(memberId);
            myBoardCollectionResponse = MyBoardCollectionResponse.of(boardMemberList);
        } else {
            List<MyBoardWorkspaceCollectionDto> boardMemberList = boardSearchRepository.searchBoardsAndWorkspaces(
                    keyword,
                    memberId
            );
            myBoardCollectionResponse = new MyBoardCollectionResponse(boardMemberList);
        }

        return myBoardCollectionResponse;
    }
//    1730955028912

    @Override
    public List<BoardActivityDetailResponseDto> getBoardActivity(Long boardId) {
        List<BoardHistory> boardHistoryCollection = boardHistoryRepository.findByWhere_BoardIdOrderByWhenDesc(boardId);
        List<CardHistory> cardHistoryCollectionByBoard = cardHistoryRepository.findByWhere_BoardIdOrderByWhenDesc(boardId);

        List<BoardActivityDetailResponseDto> activities = new ArrayList<>();

        // 각각의 컬렉션에서 DTO로 변환하면서 정렬된 상태 유지
        List<BoardActivityDetailResponseDto> boardDtoList = boardHistoryCollection.stream()
                .map(BoardActivityDetailResponseDto::createActivityDetailResponseDto)
                .toList();

        List<BoardActivityDetailResponseDto> cardDtoList = cardHistoryCollectionByBoard.stream()
                .map(BoardActivityDetailResponseDto::createActivityDetailResponseDto)
                .toList();

        // 병합 정렬을 수행
        /*
           이유는 boardHistoryCollection과 cardHistoryCollectionByBoard가 이미 when 필드 기준으로 정렬된 상태로 조회되기 때문입니다.
           이러한 경우, 병합 정렬 (merge sort) 방식이 훨씬 더 효율적입니다.
         */
        int i = 0, j = 0;
        while (i < boardDtoList.size() && j < cardDtoList.size()) {
            if (boardDtoList.get(i).when() >= cardDtoList.get(j).when()) {
                activities.add(boardDtoList.get(i++));
            } else {
                activities.add(cardDtoList.get(j++));
            }
        }

        // 나머지 요소 추가
        while (i < boardDtoList.size()) {
            activities.add(boardDtoList.get(i++));
        }
        while (j < cardDtoList.size()) {
            activities.add(cardDtoList.get(j++));
        }

        if (activities.isEmpty()) {
            return new ArrayList<>();
        }
        return activities;
    }


    @Override
    public PageBoardReplyResponseDto getRepliesByBoardId(Long boardId, Pageable pageable) {

        // 보드가 존재하는지 검증
        if (!boardRepository.existsById(boardId)) {
            throw new BoardNotFoundException(boardId);
        }

        // 페이징된 댓글을 가져옴
        Page<Reply> replyPage = replyRepository.findAllByBoardId(boardId, pageable);

        // Reply 엔티티를 BoardReplyCollectionResponseDto로 변환
        List<BoardReplyCollectionResponseDto> dtoList = replyPage.getContent().stream()
                .map(this::convertToDto)
                .toList();

        // PageBoardReplyResponseDto에 변환한 DTO 리스트와 페이징 정보를 담아 반환
        return new PageBoardReplyResponseDto(dtoList, replyPage.getTotalPages(), replyPage.getTotalElements());
    }

    private BoardReplyCollectionResponseDto convertToDto(Reply reply) {
        return new BoardReplyCollectionResponseDto(
                reply.getId(),
                reply.getContent(),
                reply.getUpdatedAt(),
                reply.getMember().getId(),
                reply.getMember().getEmail(),
                reply.getMember().getNickname(),
                reply.getMember().getProfileImgUrl(),
                reply.getCard().getId(),
                reply.getCard().getName(),
                reply.getCard().getList().getId(),
                reply.getCard().getList().getName()
        );
    }


}
