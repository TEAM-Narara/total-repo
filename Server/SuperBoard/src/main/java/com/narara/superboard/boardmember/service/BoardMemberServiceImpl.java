package com.narara.superboard.boardmember.service;

import com.narara.superboard.boardmember.infrastructure.BoardMemberRepository;
import com.narara.superboard.boardmember.interfaces.dto.BoardMemberCollectionResponseDto;
import com.narara.superboard.boardmember.entity.BoardMember;
import com.narara.superboard.boardmember.interfaces.dto.BoardMemberResponseDto;
import java.util.ArrayList;
import java.util.List;

import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.member.entity.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardMemberServiceImpl implements BoardMemberService{
    
    private final BoardMemberRepository boardMemberRepository;
    
    @Override
    public BoardMemberCollectionResponseDto getBoardMemberCollectionResponseDto(Long boardId) {
        List<BoardMember> BoardMemberList = boardMemberRepository.findAllByBoardId(boardId);

        List<BoardMemberResponseDto> boardDetailResponseDtoList = new ArrayList<>();

        for (BoardMember boardMember : BoardMemberList) {
            BoardMemberResponseDto boardMemberDetailResponseDto =
                    BoardMemberResponseDto.builder()
                            .memberId(boardMember.getMember().getId())
                            .memberEmail(boardMember.getMember().getEmail())
                            .memberNickname(boardMember.getMember().getNickname())
                            .memberProfileImgUrl(boardMember.getMember().getProfileImgUrl())
                            .authority(boardMember.getAuthority().getValue())
                            .build();

            boardDetailResponseDtoList.add(boardMemberDetailResponseDto);
        }

        return new BoardMemberCollectionResponseDto(boardDetailResponseDtoList);
    }

    public BoardMember getBoardMember(Long boardId, Member member) {
        return boardMemberRepository.findByBoardIdAndMember(boardId, member)
                .orElseThrow(() -> new NotFoundEntityException("보드", boardId, "멤버", member.getId(), "보드의 멤버"));
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
}
