package com.narara.superboard.boardmember.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.narara.superboard.board.document.BoardHistory;
import com.narara.superboard.board.entity.Board;
import com.narara.superboard.board.enums.Visibility;
import com.narara.superboard.board.infrastructure.BoardHistoryRepository;
import com.narara.superboard.board.infrastructure.BoardRepository;
import com.narara.superboard.board.service.kafka.BoardOffsetService;
import com.narara.superboard.boardmember.infrastructure.BoardMemberRepository;
import com.narara.superboard.boardmember.interfaces.dto.BoardMemberResponseDto;
import com.narara.superboard.boardmember.entity.BoardMember;
import com.narara.superboard.boardmember.interfaces.dto.MemberResponseDto;

import com.narara.superboard.fcmtoken.service.FcmTokenService;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.narara.superboard.boardmember.interfaces.dto.log.AddBoardMemberInfo;
import com.narara.superboard.boardmember.interfaces.dto.log.DeleteBoardMemberInfo;
import com.narara.superboard.common.constant.enums.Authority;
import com.narara.superboard.common.constant.enums.EventData;
import com.narara.superboard.common.constant.enums.EventType;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.infrastructure.MemberRepository;
import com.narara.superboard.workspace.entity.WorkSpace;
import com.narara.superboard.workspacemember.infrastructure.WorkSpaceMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class BoardMemberServiceImpl implements BoardMemberService {

    private final BoardMemberRepository boardMemberRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final WorkSpaceMemberRepository workSpaceMemberRepository;
    private final BoardHistoryRepository boardHistoryRepository;

    private final BoardOffsetService boardOffsetService;

    private final FcmTokenService fcmTokenService;

    @Override
    public BoardMemberResponseDto getBoardMemberCollectionResponseDto(Long boardId) {
        Board board = boardRepository.findByIdAndIsDeletedFalse(boardId)
                .orElseThrow(() -> new NotFoundEntityException(boardId, "board"));
        WorkSpace workSpace = board.getWorkSpace();

        List<Member> boardMemberList = boardMemberRepository.findAllMembersByBoardId(boardId);

        List<MemberResponseDto> boardDetailResponseDtoList = getMemberResponseDtos(boardMemberList);
        List<MemberResponseDto> workspaceDetailResponseDtoList = new ArrayList<>();

        if (board.getVisibility().equals(Visibility.WORKSPACE)) {
            List<Member> workspaceMemberList = workSpaceMemberRepository.findAllMembersByWorkspaceId(workSpace.getId());

            // 3. Board 멤버를 Set으로 변환 (contains 연산 최적화)
            Set<Member> boardMemberSet = new HashSet<>(boardMemberList);

            // 4. Workspace에만 속한 멤버 필터링 (Board 멤버가 아닌 멤버만 선택)
            List<Member> workspaceOnlyMembers = workspaceMemberList.stream()
                    .filter(workspaceMember -> !boardMemberSet.contains(workspaceMember))
                    .collect(Collectors.toList());

            workspaceDetailResponseDtoList = getMemberResponseDtos(workspaceOnlyMembers);
        }

        return new BoardMemberResponseDto(
                workspaceDetailResponseDtoList,
                boardDetailResponseDtoList
        );
    }

    private static List<MemberResponseDto> getMemberResponseDtos(List<Member> workspaceMemberList) {
        List<MemberResponseDto> boardDetailResponseDtoList;
        boardDetailResponseDtoList = new ArrayList<>();

        for (Member member : workspaceMemberList) {
            MemberResponseDto boardMemberDetailResponseDto =
                    MemberResponseDto.builder()
                            .memberId(member.getId())
                            .memberEmail(member.getEmail())
                            .memberNickname(member.getNickname())
                            .memberProfileImgUrl(member.getProfileImgUrl())
//                                .authority(boardMember.getAuthority().toString())
//                                .isDeleted(boardMember.getIsDeleted())
                            .build();

            boardDetailResponseDtoList.add(boardMemberDetailResponseDto);
        }
        return boardDetailResponseDtoList;
    }

    public BoardMember getBoardMember(Long boardId, Member member) {
        return boardMemberRepository.findByBoardIdAndMemberAndIsDeletedIsFalse(boardId, member)
                .orElseThrow(() -> new NotFoundEntityException("보드", boardId, "멤버", member.getId(), "보드의 멤버"));
    }

    public BoardMember getBoardMember(Board board, Member member) {
        return boardMemberRepository.findFirstByBoardAndMemberAndIsDeletedIsFalse(board, member)
                .orElseGet(() -> null);
    }

    // 알림 설정 조회
    @Override
    @Transactional()
    public Boolean getWatchStatus(Long boardId, Member member) {
        BoardMember boardMember = getBoardMember(boardId, member);
        return boardMember.isAlert();
    }

    // 알림 설정 수정
    @Override
    @Transactional
    public void updateWatchStatus(Long boardId, Member member) {
        BoardMember boardMember = getBoardMember(boardId, member);
        boardMember.changeIsAlert();
    }

    @Override
    @Transactional
    public BoardMember addBoardMember(Member member, Long boardId, Long inviteMemberId)
            throws FirebaseMessagingException {
        Board board = getBoard(boardId);
        Member inviteMember = getMember(inviteMemberId);
        BoardMember boardMember = getBoardMember(board, inviteMember);

        //이미 보드멤버가 있으면 무시
        if (boardMember != null) {
            return boardMember;
        }

        //보드멤버가 없으면 새로 만들기
        BoardMember newBoardMember = BoardMember.createBoardMemberByAdmin(board, inviteMember);
        boardMemberRepository.save(newBoardMember);
        boardOffsetService.saveAddBoardMemberDiff(newBoardMember); //Websocket 보드멤버 추가

        // 멤버 추가 로그 기록
        AddBoardMemberInfo addBoardMemberInfo = new AddBoardMemberInfo(inviteMember.getId(), inviteMember.getNickname(), boardId, board.getName());

        BoardHistory<AddBoardMemberInfo> boardHistory = BoardHistory.createBoardHistory(
                inviteMember, LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toEpochSecond()
                , board, EventType.ADD, EventData.BOARD_MEMBER, addBoardMemberInfo);

        boardHistoryRepository.save(boardHistory);

        //[알림]
        sendAddMemberAlarm(member, boardMember);

        return newBoardMember;
    }

    private void sendAddMemberAlarm(Member manOfAction, BoardMember boardMember) throws FirebaseMessagingException {
        Board board = boardMember.getBoard();
        WorkSpace workSpace = board.getWorkSpace();

        HashMap<String, String> data = new HashMap<>();
        data.put("type", "ME_ADD_BOARD_MEMBER");
        data.put("goTo", "BOARD");
        data.put("workspaceId", String.valueOf(workSpace.getId()));
        data.put("boardId", String.valueOf(board.getId()));

        //"*사용자이름* made you an admin on the board *보드이름*"
        String title = String.format("*%s* made you an %s on the board *%s*", manOfAction.getNickname(), boardMember.getAuthority().name(), board.getName());

        //대상자에게만 알람
        fcmTokenService.sendMessage(boardMember.getMember(), title, "", data);
    }

    private Board getBoard(Long boardId) {
        return boardRepository.findByIdAndIsDeletedFalse(boardId)
                .orElseThrow(() -> new NotFoundEntityException(boardId, "Borad"));
    }

    private Member getMember(Long inviteMemberId) {
        return memberRepository.findByIdAndIsDeletedFalse(inviteMemberId)
                .orElseThrow(() -> new NotFoundEntityException(inviteMemberId, "Member"));
    }

    @Override
    @Transactional
    public BoardMember editBoardMemberAuthority(Long boardId, Long editMemberId, Authority authority) {
        Board board = getBoard(boardId);
        Member editMember = getMember(editMemberId);
        BoardMember boardMember = getBoardMember(board, editMember);

        boardMember.editAuthority(authority);
        boardOffsetService.saveEditBoardMemberDiff(boardMember); //Websocket 보드멤버 권한 수정

        return boardMember;
    }

    @Override
    @Transactional
    public BoardMember deleteMember(Long boardId, Long deleteMemberId) {
        Board board = getBoard(boardId);
        Member deleteMember = getMember(deleteMemberId);
        BoardMember boardMember = getBoardMember(board, deleteMember);

        boardMember.deleted();
        boardOffsetService.saveDeleteBoardMemberDiff(boardMember); // Websocket 보드멤버 삭제

        // 멤버 삭제 로그 기록
        DeleteBoardMemberInfo deleteBoardMemberInfo = new DeleteBoardMemberInfo(deleteMember.getId(), deleteMember.getNickname(), boardId, board.getName());

        BoardHistory<DeleteBoardMemberInfo> boardHistory = BoardHistory.createBoardHistory(
                deleteMember, LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toEpochSecond()
                , board, EventType.DELETE, EventData.BOARD_MEMBER, deleteBoardMemberInfo);

        boardHistoryRepository.save(boardHistory);

        return boardMember;
    }

}
