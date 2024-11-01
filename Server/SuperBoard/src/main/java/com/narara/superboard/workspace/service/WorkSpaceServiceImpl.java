package com.narara.superboard.workspace.service;

import com.narara.superboard.board.interfaces.dto.BoardCollectionResponseDto;
import com.narara.superboard.board.service.BoardService;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.exception.MemberNotFoundException;
import com.narara.superboard.member.infrastructure.MemberRepository;
import com.narara.superboard.workspace.exception.WorkspaceNameNotFoundException;
import com.narara.superboard.workspace.entity.WorkSpace;
import com.narara.superboard.workspace.infrastructure.WorkSpaceRepository;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceDetailResponseDto;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceCreateRequestDto;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceUpdateRequestDto;
import com.narara.superboard.workspace.service.validator.WorkSpaceValidator;
import com.narara.superboard.workspacemember.entity.WorkSpaceMember;
import com.narara.superboard.workspacemember.infrastructure.WorkSpaceMemberRepository;
import com.narara.superboard.workspacemember.interfaces.dto.WorkspaceMemberCollectionResponseDto;
import com.narara.superboard.workspacemember.service.WorkSpaceMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class WorkSpaceServiceImpl implements WorkSpaceService {

    private final WorkSpaceValidator workSpaceValidator;
    private final MemberRepository memberRepository;
    private final WorkSpaceRepository workSpaceRepository;
    private final BoardService boardService;
    private final WorkSpaceMemberService workSpaceMemberService;
    private final WorkSpaceMemberRepository workSpaceMemberRepository;

    @Override
    public WorkSpace createWorkSpace(Long memberId, WorkSpaceCreateRequestDto workspaceCreateRequestDto) throws WorkspaceNameNotFoundException {
        workSpaceValidator.validateNameIsPresent(workspaceCreateRequestDto);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        WorkSpace workSpace = WorkSpace.createWorkSpace(workspaceCreateRequestDto);

        WorkSpace newWorkSpace = workSpaceRepository.save(workSpace);
        WorkSpaceMember workspaceMemberByAdmin = WorkSpaceMember.createWorkspaceMemberByAdmin(newWorkSpace, member);
        workSpaceMemberRepository.save(workspaceMemberByAdmin);
        return newWorkSpace;
    }

    @Override
    public WorkSpace updateWorkSpace(Long workSpaceId, WorkSpaceUpdateRequestDto workspaceUpdateRequestDto) throws WorkspaceNameNotFoundException{
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
    public WorkSpaceDetailResponseDto getWorkspaceDetail(Long workSpaceId) {
        WorkSpace workSpace = getWorkSpace(workSpaceId);

        BoardCollectionResponseDto boardCollectionResponseDto =
                boardService.getBoardCollectionResponseDto(workSpaceId);
        WorkspaceMemberCollectionResponseDto workspaceMemberCollectionResponseDto =
                workSpaceMemberService.getWorkspaceMemberCollectionResponseDto(workSpaceId);

        WorkSpaceDetailResponseDto workspaceDetailResponseDto = WorkSpaceDetailResponseDto.builder()
                .workSpaceId(workSpace.getId())
                .name(workSpace.getName())
                .boardList(boardCollectionResponseDto)
                .workspaceMemberList(workspaceMemberCollectionResponseDto)
                .build();

        workSpaceValidator.validateNameIsPresent(workspaceDetailResponseDto);

        return workspaceDetailResponseDto;
    }
}
