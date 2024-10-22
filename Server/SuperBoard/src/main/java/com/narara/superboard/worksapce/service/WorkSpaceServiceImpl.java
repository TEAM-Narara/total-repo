package com.narara.superboard.worksapce.service;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.board.infrastrucutre.BoardRepository;
import com.narara.superboard.board.interfaces.dto.BoardCollectionResponseDto;
import com.narara.superboard.board.interfaces.dto.BoardDetailResponseDto;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.common.exception.WorkspaceNameNotFoundException;
import com.narara.superboard.worksapce.entity.WorkSpace;
import com.narara.superboard.worksapce.infrastructure.WorkSpaceRepository;
import com.narara.superboard.worksapce.interfaces.dto.WorkspaceDetailResponseDto;
import com.narara.superboard.worksapce.interfaces.dto.WorkspaceRequestCreateDto;
import com.narara.superboard.worksapce.interfaces.dto.WorkspaceUpdateRequestDto;
import com.narara.superboard.worksapce.service.validator.WorkSpaceValidator;
import com.narara.superboard.workspacemember.entity.WorkspaceMember;
import com.narara.superboard.workspacemember.infrastructure.WorkSpaceMemberRepository;
import com.narara.superboard.workspacemember.interfaces.dto.WorkspaceMemberCollectionResponseDto;
import com.narara.superboard.workspacemember.interfaces.dto.WorkspaceMemberDetailResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkSpaceServiceImpl implements WorkSpaceService {

    private final WorkSpaceValidator workSpaceValidator;

    private final WorkSpaceRepository workSpaceRepository;
    private final BoardRepository boardRepository;
    private final WorkSpaceMemberRepository workSpaceMemberRepository;

    @Override
    public void createWorkSpace(WorkspaceRequestCreateDto workspaceRequestCreateDto) throws WorkspaceNameNotFoundException {
        workSpaceValidator.validateNameIsPresent(workspaceRequestCreateDto);

        WorkSpace workSpace = WorkSpace.createWorkSpace(workspaceRequestCreateDto);
        workSpaceRepository.save(workSpace);
    }

    @Override
    public WorkSpace updateWorkSpace(Long workSpaceId, WorkspaceUpdateRequestDto workspaceUpdateRequestDto) throws WorkspaceNameNotFoundException{
        workSpaceValidator.validateNameIsPresent(workspaceUpdateRequestDto);

        WorkSpace workSpace = getWorkSpace(workSpaceId);

        return workSpace.updateWorkSpace(workspaceUpdateRequestDto);
    }

    @Override
    public void deleteWorkSpace(Long workSpaceId) {
        WorkSpace workSpace = getWorkSpace(workSpaceId);
        workSpaceRepository.delete(workSpace);
    }

    @Override
    public WorkSpace getWorkSpace(Long workSpaceId) {
        return workSpaceRepository.findById(workSpaceId)
                .orElseThrow(() -> new NotFoundEntityException(workSpaceId, "WorkSpace"));
    }

    @Override
    public WorkspaceDetailResponseDto getWorkspaceDetail(Long workSpaceId) {
        WorkSpace workSpace = getWorkSpace(workSpaceId);
        List<Board> BoardList = boardRepository.findByAllWorkspaceId(workSpaceId);
        List<WorkspaceMember> WorkSpaceMemberList = workSpaceMemberRepository.findByAllWorkspaceId(workSpaceId);

        List<BoardDetailResponseDto> boardDetailResponseDtoList= new ArrayList<>();

        for (Board board : BoardList) {
            BoardDetailResponseDto boardDto = BoardDetailResponseDto.builder()
                    .id(board.getId())
                    .name(board.getName())
                    .backgroundType(board.getBackGroundType())
                    .backgroundValue(board.getBackGroundType())
                    .build();

            boardDetailResponseDtoList.add(boardDto);
        }

        BoardCollectionResponseDto boardCollectionResponseDto =
                new BoardCollectionResponseDto(boardDetailResponseDtoList);

        List<WorkspaceMemberDetailResponseDto> workspaceDetailResponseDtoList = new ArrayList<>();

        for (WorkSpaceMember workSpaceMember : WorkSpaceMemberList) {
            WorkspaceMemberDetailResponseDto workspaceMemberDetailResponseDto =
                    WorkspaceMemberDetailResponseDto.builder()
                    .id(workSpaceMember.getId())
                    .email(workSpaceMember.getEmail())
                    .name(workSpaceMember.getName())
                    .profileImgUrl(workSpaceMember.getProfileImgUrl())
                    .authority(workSpaceMember.getAuthority())
                    .build();

            workspaceDetailResponseDtoList.add(workspaceMemberDetailResponseDto);
        }

        WorkspaceMemberCollectionResponseDto workspaceMemberCollectionResponseDto =
                new WorkspaceMemberCollectionResponseDto(workspaceDetailResponseDtoList);



        return WorkspaceDetailResponseDto.builder()
                .workSpaceId(workSpace.getId())
                .name(workSpace.getName())
                .boardList(boardCollectionResponseDto)
                .workspaceMemberList(workspaceMemberCollectionResponseDto)
                .build();
    }
}
