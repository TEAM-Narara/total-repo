package com.narara.superboard.workspace.service;

import com.narara.superboard.board.interfaces.dto.BoardCollectionResponseDto;
import com.narara.superboard.board.interfaces.dto.BoardDetailResponseDto;
import com.narara.superboard.board.service.BoardService;
import com.narara.superboard.boardmember.interfaces.dto.MemberCollectionResponseDto;
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
import com.narara.superboard.workspace.service.mongo.WorkspaceOffsetService;
import com.narara.superboard.workspace.service.validator.WorkSpaceValidator;
import com.narara.superboard.workspacemember.entity.WorkSpaceMember;
import com.narara.superboard.workspacemember.infrastructure.WorkSpaceMemberRepository;
import com.narara.superboard.workspacemember.service.WorkSpaceMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class WorkSpaceServiceImpl implements WorkSpaceService {
    private final WorkSpaceValidator workSpaceValidator;
    private final MemberRepository memberRepository;
    private final WorkSpaceRepository workSpaceRepository;
    private final BoardService boardService;
    private final WorkSpaceMemberService workSpaceMemberService;
    private final WorkSpaceMemberRepository workSpaceMemberRepository;
    private final WorkspaceOffsetService workspaceOffsetService;

    @Override
    @Transactional
    public WorkSpace createWorkSpace(Long memberId, WorkSpaceCreateRequestDto workspaceCreateRequestDto) throws WorkspaceNameNotFoundException {
        workSpaceValidator.validateNameIsPresent(workspaceCreateRequestDto);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        WorkSpace workSpace = WorkSpace.createWorkSpace(workspaceCreateRequestDto);

        WorkSpace newWorkSpace = workSpaceRepository.save(workSpace);
        WorkSpaceMember workspaceMemberByAdmin = WorkSpaceMember.createWorkspaceMemberByAdmin(newWorkSpace, member); //offset++
        workSpaceMemberRepository.save(workspaceMemberByAdmin);

        return newWorkSpace;
    }

    @Override
    @Transactional
    public void deleteWorkSpace(Long workSpaceId) {
        WorkSpace workSpace = getWorkSpace(workSpaceId);
        workSpace.deleted(); //삭제 처리 offset++

//        workspaceOffsetService.saveDeleteWorkspaceDiff(workSpace);
    }

    @Override
    public WorkSpace getWorkSpace(Long workSpaceId) {
        return workSpaceRepository.findById(workSpaceId)
                .orElseThrow(() -> new NotFoundEntityException(workSpaceId, "WorkSpace"));
    }

    @Override
    public WorkSpaceDetailResponseDto getWorkspaceDetail(Long workSpaceId) {
        WorkSpace workSpace = getWorkSpace(workSpaceId);

        List<BoardDetailResponseDto> boardCollectionResponseDto =
                boardService.getBoardCollectionResponseDto(workSpaceId);

        MemberCollectionResponseDto workspaceMemberCollectionResponseDto =
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

    @Override
    public List<WorkSpace> getWorkspaceByMember(Long memberId) {
        List<WorkSpaceMember> allByMemberId = workSpaceMemberRepository.findAllByMemberId(memberId);
        List<WorkSpace> workspaceList = new ArrayList<>();
        for (WorkSpaceMember workSpaceMember: allByMemberId) {
            workspaceList.add(workSpaceMember.getWorkSpace());
        }

        return workspaceList;
    }

    @Transactional
    @Override
    public WorkSpace updateWorkSpace(Long workspaceId, String name) {
        workSpaceValidator.validateNameIsPresent(new WorkSpaceUpdateRequestDto(name));
        WorkSpace workSpace = getWorkSpace(workspaceId);
        workSpace.updateWorkSpace(name); //offset++

        workspaceOffsetService.saveEditWorkspaceDiff(workSpace);

        return workSpace;
    }
}
