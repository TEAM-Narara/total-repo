package com.narara.superboard.board.service;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.board.exception.BoardNotFoundException;
import com.narara.superboard.board.infrastructure.BoardRepository;
import com.narara.superboard.board.infrastructure.BoardSearchRepository;
import com.narara.superboard.board.interfaces.dto.*;
import com.narara.superboard.board.service.validator.BoardValidator;
import com.narara.superboard.boardmember.entity.BoardMember;
import com.narara.superboard.boardmember.infrastructure.BoardMemberRepository;
import com.narara.superboard.card.infrastructure.CardRepository;
import com.narara.superboard.common.application.handler.CoverHandler;
import com.narara.superboard.common.application.validator.CoverValidator;
import com.narara.superboard.common.application.validator.NameValidator;
import com.narara.superboard.common.constant.enums.Authority;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.list.infrastructure.ListRepository;
import com.narara.superboard.common.exception.authority.UnauthorizedException;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final WorkSpaceRepository workspaceRepository;
    private final BoardMemberRepository boardMemberRepository;
    private final WorkspaceOffsetService workspaceOffsetService;

    private final BoardValidator boardValidator;
    private final CoverValidator coverValidator;
    private final NameValidator nameValidator;

    private final CoverHandler coverHandler;
    private final MemberRepository memberRepository;

    private final ListRepository listRepository;
    private final ReplyRepository replyRepository;
    private final CardRepository cardRepository;
    private final BoardSearchRepository boardSearchRepository;

    @Override
    public List<BoardDetailResponseDto> getBoardCollectionResponseDto(Long workSpaceId) {
        List<Board> BoardList = boardRepository.findAllByWorkSpaceId(workSpaceId);

        List<BoardDetailResponseDto> boardDetailResponseDtoList = new ArrayList<>();

        for (Board board : BoardList) {
            BoardDetailResponseDto boardDto = BoardDetailResponseDto.builder()
                    .id(board.getId())
                    .name(board.getName())
                    .backgroundType(coverHandler.getTypeValue(board.getCover()))
                    .backgroundValue(coverHandler.getValue(board.getCover()))
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
        // TODO: background가 존재하면 background에 대한 검증 추가하기

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        WorkSpace workSpace = workspaceRepository.findById(boardCreateRequestDto.workSpaceId())
                .orElseThrow(() -> new NotFoundEntityException(boardCreateRequestDto.workSpaceId(), "워크스페이스"));

        Board board = Board.createBoard(boardCreateRequestDto, workSpace);

        Board saveBoard = boardRepository.save(board);
        BoardMember boardMemberByAdmin = BoardMember.createBoardMemberByAdmin(saveBoard, member);
        boardMemberRepository.save(boardMemberByAdmin);

        //보드 추가의 경우, workspace 구독 시 정보를 받을 수 있다
        board.getWorkSpace().addOffset(); //workspace offset++
        workspaceOffsetService.saveAddBoardDiff(board);

        return saveBoard;
    }

    @Override
    public Board getBoard(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new NotFoundEntityException(boardId, "Board"));
    }

    @Override
    public void deleteBoard(Long boardId) {
        Board board = getBoard(boardId);
        board.deleted();

        //보드 삭제(닫기)의 경우, workspace 구독 시 정보를 받을 수 있다
        board.getWorkSpace().addOffset();
        workspaceOffsetService.saveDeleteBoardDiff(board);
    }

    @Override
    public Board updateBoard(Long memberId, Long boardId, BoardUpdateRequestDto boardUpdateRequestDto) {
        boardValidator.validateNameIsPresent(boardUpdateRequestDto);
        boardValidator.validateVisibilityIsPresent(boardUpdateRequestDto);
        boardValidator.validateVisibilityIsValid(boardUpdateRequestDto);

        if (boardUpdateRequestDto.cover() != null) {
            coverValidator.validateCoverTypeIsValid(boardUpdateRequestDto.cover());
        }

        BoardMember boardMember = boardMemberRepository.findFirstByBoard_IdAndMember_Id(boardId, memberId)
                .orElseThrow(() -> new AccessDeniedException("보드에 대한 권한이 없습니다"));

        Board board = getBoard(boardId);

        // 권한 검사를 할 때 Authority가 null인 경우 AccessDeniedException을 던지도록 추가
        if (boardMember.getAuthority() == null) {
            throw new AccessDeniedException("보드에 대한 권한이 잘못되었습니다.");
        }

        if (boardMember.getAuthority().equals(Authority.MEMBER)) {
            return board.updateBoardByMember(boardUpdateRequestDto);
        } else if (boardMember.getAuthority().equals(Authority.ADMIN)) {
            return board.updateBoardByAdmin(boardUpdateRequestDto);
        }

        throw new AccessDeniedException("보드에 대한 권한이 잘못되었습니다.");
    }

    // 아카이브된 보드 리스트 조회
    @Override
    public List<Board> getArchivedBoards(Long workspaceId) {
        return boardRepository.findAllByWorkSpaceIdAndIsArchivedTrue(workspaceId);
    }

    // 보드 아카이브 상태 변경
    @Override
    public void changeArchiveStatus(Long boardId) {
        Board board = getBoard(boardId);
        board.changeArchiveStatus();
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
                    keyword, memberId);
            myBoardCollectionResponse = new MyBoardCollectionResponse(boardMemberList);
        }

        return myBoardCollectionResponse;
    }

    @Override
    public PageBoardReplyResponseDto getRepliesByBoardId(Long boardId, Pageable pageable) {

        // 보드가 존재하는지 검증
        boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException(boardId));

        // 페이징된 댓글을 가져옴
        Page<Reply> replyPage = replyRepository.findAllByBoardId(boardId, pageable);

        // Reply 엔티티를 BoardReplyCollectionResponseDto로 변환
        List<BoardReplyCollectionResponseDto> dtoList = replyPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

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
