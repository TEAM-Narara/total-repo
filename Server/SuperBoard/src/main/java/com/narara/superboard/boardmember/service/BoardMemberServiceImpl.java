package com.narara.superboard.boardmember.service;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.board.infrastructure.BoardRepository;
import com.narara.superboard.boardmember.infrastructure.BoardMemberRepository;
import com.narara.superboard.boardmember.interfaces.dto.MemberCollectionResponseDto;
import com.narara.superboard.boardmember.entity.BoardMember;
import com.narara.superboard.boardmember.interfaces.dto.MemberResponseDto;
import java.util.ArrayList;
import java.util.List;

import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.infrastructure.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardMemberServiceImpl implements BoardMemberService{
    
    private final BoardMemberRepository boardMemberRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    @Override
    public MemberCollectionResponseDto getBoardMemberCollectionResponseDto(Long boardId) {
        List<BoardMember> BoardMemberList = boardMemberRepository.findAllByBoardId(boardId);

        List<MemberResponseDto> boardDetailResponseDtoList = new ArrayList<>();

        for (BoardMember boardMember : BoardMemberList) {
            MemberResponseDto boardMemberDetailResponseDto =
                    MemberResponseDto.builder()
                            .memberId(boardMember.getMember().getId())
                            .memberEmail(boardMember.getMember().getEmail())
                            .memberNickname(boardMember.getMember().getNickname())
                            .memberProfileImgUrl(boardMember.getMember().getProfileImgUrl())
                            .authority(boardMember.getAuthority().toString())
                            .build();

            boardDetailResponseDtoList.add(boardMemberDetailResponseDto);
        }

        return new MemberCollectionResponseDto(boardDetailResponseDtoList);
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
    public BoardMember addBoardMember(Long boardId, Long inviteMemberId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new NotFoundEntityException(boardId, "Borad"));

        Member inviteMember = memberRepository.findById(inviteMemberId)
                .orElseThrow(() -> new NotFoundEntityException(inviteMemberId, "Member"));

        BoardMember boardMember = getBoardMember(board, inviteMember);

        //이미 보드멤버가 있으면 무시
        if (boardMember != null) {
            return boardMember;
        }

        //보드멤버가 없으면 새로 만들기
        BoardMember newBoardMember = BoardMember.createBoardMemberByAdmin(board, inviteMember);
        boardMemberRepository.save(newBoardMember);

        return newBoardMember;
    }
}
