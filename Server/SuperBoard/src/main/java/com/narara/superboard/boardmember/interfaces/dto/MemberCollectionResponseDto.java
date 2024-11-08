package com.narara.superboard.boardmember.interfaces.dto;

import com.narara.superboard.workspacemember.entity.WorkSpaceMember;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public record MemberCollectionResponseDto(List<MemberResponseDto> memberListResponse) {
    public static MemberCollectionResponseDto from(List<WorkSpaceMember> workSpaceMemberList) {
        List<MemberResponseDto> memberResponseDtoList = new ArrayList<>();
        for (WorkSpaceMember workSpaceMember : workSpaceMemberList) {
            memberResponseDtoList.add(
                    MemberResponseDto.builder()
                            .memberId(workSpaceMember.getMember().getId())
                            .memberEmail(workSpaceMember.getMember().getEmail())
                            .memberNickname(workSpaceMember.getMember().getNickname())
                            .memberProfileImgUrl(workSpaceMember.getMember().getProfileImgUrl())
                            .authority(workSpaceMember.getAuthority().toString())
                            .isDeleted(workSpaceMember.getIsDeleted())
                            .build()
            );
        }

        return new MemberCollectionResponseDto(memberResponseDtoList);
    }
}
