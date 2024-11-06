package com.narara.superboard.workspacemember.service;

import com.narara.superboard.boardmember.interfaces.dto.MemberCollectionResponseDto;
import com.narara.superboard.boardmember.interfaces.dto.MemberResponseDto;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.common.constant.enums.Authority;
import com.narara.superboard.member.exception.MemberNotFoundException;
import com.narara.superboard.member.infrastructure.MemberRepository;
import com.narara.superboard.workspace.entity.WorkSpace;
import com.narara.superboard.workspace.infrastructure.WorkSpaceRepository;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceListResponseDto;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceResponseDto;
import com.narara.superboard.workspace.service.validator.WorkSpaceValidator;
import com.narara.superboard.workspacemember.entity.WorkSpaceMember;
import com.narara.superboard.workspacemember.exception.EmptyWorkspaceMemberException;
import com.narara.superboard.workspacemember.infrastructure.WorkSpaceMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class WorkSpaceMemberServiceImpl implements WorkSpaceMemberService {
    private final WorkSpaceMemberRepository workSpaceMemberRepository;
    private final WorkSpaceRepository workSpaceRepository;
    private final WorkSpaceValidator workSpaceValidator;
    private final MemberRepository memberRepository;
//    private final WorkspaceOffsetService workspaceOffsetService;

    @Override
    public MemberCollectionResponseDto getWorkspaceMemberCollectionResponseDto(Long workSpaceId) {
        List<WorkSpaceMember> WorkSpaceMemberList = workSpaceMemberRepository.findAllByWorkSpaceId(workSpaceId);

        List<MemberResponseDto> workspaceDetailResponseDtoList = new ArrayList<>();

        for (WorkSpaceMember workSpaceMember : WorkSpaceMemberList) {
            MemberResponseDto dto = MemberResponseDto.builder()
                    .memberId(workSpaceMember.getMember().getId())
                    .memberEmail(workSpaceMember.getMember().getEmail())
                    .memberNickname(workSpaceMember.getMember().getNickname())
                    .memberProfileImgUrl(workSpaceMember.getMember().getProfileImgUrl())
                    .authority(workSpaceMember.getAuthority().toString())
                    .isDeleted(workSpaceMember.getIsDeleted())
                    .build();

            workspaceDetailResponseDtoList.add(dto);
        }

        return new MemberCollectionResponseDto(workspaceDetailResponseDtoList);
    }

    @Override
    public WorkSpaceListResponseDto getMemberWorkspaceList(Member member) {
        List<WorkSpaceMember> workSpaceMemberList = workSpaceMemberRepository.findAllByMember(member);

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

    @Transactional
    @Override
    public WorkSpaceMember editAuthority(Long memberId, Long workspaceId, Authority authority) {
        WorkSpaceMember workSpaceMember = getWorkSpaceMember(memberId, workspaceId);
        workSpaceMember.editAuthority(authority);
        workSpaceMember.getWorkSpace().addOffset(); //workspace offset++

//        workspaceOffsetService.saveEditMemberDiff(workSpaceMember);

        workSpaceMember.getWorkSpace().addOffset();

        return workSpaceMember;
    }

    private WorkSpaceMember getWorkSpaceMember(Long workspaceId, Long memberId) {
        return workSpaceMemberRepository.findFirstByWorkSpaceIdAndMemberId(workspaceId, memberId)
                .orElseThrow(() -> new NoSuchElementException("찾을 수 없습니다"));
    }

    @Transactional
    @Override
    public WorkSpaceMember addMember(Long workspaceId, Long memberId, Authority authority) {
        WorkSpace workSpace = workSpaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NoSuchElementException("워크스페이스가 존재하지 않습니다"));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        //1. 워크스페이스에 이미 멤버가 추가되어 있으면 무시
        WorkSpaceMember workspaceMember = workSpaceMemberRepository.findFirstByWorkSpaceIdAndMemberId(workspaceId,
                        memberId)
                .orElse(null);

        if (workspaceMember != null) {
            return workspaceMember;
        }

        //2. 워크스페이스에 멤버가 추가되어 있지 않다면 새로생성
        WorkSpaceMember workSpaceMember = WorkSpaceMember.builder()
                .workSpace(workSpace)
                .member(member)
                .authority(authority)
                .build();

        workSpaceMemberRepository.save(workSpaceMember);
        workSpace.addOffset(); //workspace offset++

//        workspaceOffsetService.saveAddMemberDiff(workspaceMember);

        return workSpaceMember;
    }

    @Transactional
    @Override
    public WorkSpaceMember deleteMember(Long workspaceId, Long memberId) {
        WorkSpaceMember workSpaceMember = getWorkSpaceMember(workspaceId, memberId);
        WorkSpace workSpace = workSpaceMember.getWorkSpace();

        boolean workspaceHasOneMember = workSpaceMemberRepository.existsByWorkSpaceAndIsDeletedIsFalse(workSpace);
        if (!workspaceHasOneMember) {
            throw new EmptyWorkspaceMemberException();
        }

        workSpaceMember.deleted();
        workSpaceMember.getWorkSpace().addOffset(); //workspace offset++

//        workspaceOffsetService.saveDeleteMemberDiff(workSpaceMember);

        return workSpaceMember;
    }

    @Override
    public List<WorkSpaceMember> getWorkspaceMember(Long workspaceId) {
        return workSpaceMemberRepository.findAllByWorkSpaceId(workspaceId);
    }
}
