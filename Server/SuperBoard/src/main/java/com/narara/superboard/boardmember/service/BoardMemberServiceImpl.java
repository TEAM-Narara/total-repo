package com.narara.superboard.boardmember.service;

import com.narara.superboard.boardmember.infrastructure.BoardMemberRepository;
import com.narara.superboard.boardmember.interfaces.dto.BoardMemberCollectionResponseDto;
import com.narara.superboard.boardmember.entity.BoardMember;
import com.narara.superboard.boardmember.interfaces.dto.BoardMemberResponseDto;
import java.util.ArrayList;
import java.util.List;
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
                            .authority(boardMember.getAuthority())
                            .build();

            boardDetailResponseDtoList.add(boardMemberDetailResponseDto);
        }

        return new BoardMemberCollectionResponseDto(boardDetailResponseDtoList);
    }
}
