package com.narara.superboard.workspacemember.service;

import com.narara.superboard.workspace.entity.WorkSpace;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceListResponseDto;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceResponseDto;
import com.narara.superboard.workspace.service.validator.WorkSpaceValidator;
import com.narara.superboard.workspacemember.entity.WorkSpaceMember;
import com.narara.superboard.workspacemember.infrastructure.WorkSpaceMemberRepository;
import com.narara.superboard.workspacemember.interfaces.dto.WorkspaceMemberCollectionResponseDto;
import com.narara.superboard.workspacemember.interfaces.dto.WorkSpaceMemberDetailResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkSpaceMemberServiceImpl implements WorkSpaceMemberService {

    private final WorkSpaceMemberRepository workSpaceMemberRepository;
    private final WorkSpaceValidator workSpaceValidator;

    @Override
    public WorkspaceMemberCollectionResponseDto getWorkspaceMemberCollectionResponseDto(Long workSpaceId) {
        List<WorkSpaceMember> WorkSpaceMemberList = workSpaceMemberRepository.findAllByWorkSpaceId(workSpaceId);

        List<WorkSpaceMemberDetailResponseDto> workspaceDetailResponseDtoList = new ArrayList<>();

        for (WorkSpaceMember workSpaceMember : WorkSpaceMemberList) {
            WorkSpaceMemberDetailResponseDto workspaceMemberDetailResponseDto =
                    WorkSpaceMemberDetailResponseDto.builder()
                            .memberId(workSpaceMember.getMember().getId())
                            .memberEmail(workSpaceMember.getMember().getEmail())
                            .memberNickname(workSpaceMember.getMember().getNickname())
                            .memberProfileImgUrl(workSpaceMember.getMember().getProfileImgUrl())
                            .authority(workSpaceMember.getAuthority().toString())
                            .build();

            workspaceDetailResponseDtoList.add(workspaceMemberDetailResponseDto);
        }

        return new WorkspaceMemberCollectionResponseDto(workspaceDetailResponseDtoList);
    }

    @Override
    public WorkSpaceListResponseDto getMemberWorkspaceList(Long memberId) {
        List<WorkSpaceMember> workSpaceMemberList = workSpaceMemberRepository.findAllByMemberId(memberId);

        List<WorkSpaceResponseDto> workSpaceResponseDtoList = new ArrayList<>();

        for (WorkSpaceMember workSpaceMember : workSpaceMemberList) {
            WorkSpace workSpace = workSpaceMember.getWorkSpace();
            WorkSpaceResponseDto workSpaceResponseDto = WorkSpaceResponseDto.builder()
                    .workSpaceId(workSpace.getId())
                    .name(workSpace.getName())
                    .build();

            workSpaceValidator.validateNameIsPresent(workSpaceResponseDto);

            workSpaceResponseDtoList.add(workSpaceResponseDto);
        }

        return WorkSpaceListResponseDto.builder()
                .workSpaceResponseDtoList(workSpaceResponseDtoList).build();
    }
}
