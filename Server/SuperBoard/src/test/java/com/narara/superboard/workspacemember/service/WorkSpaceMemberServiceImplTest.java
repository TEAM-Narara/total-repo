package com.narara.superboard.workspacemember.service;

import com.narara.superboard.MockSuperBoardUnitTests;
import com.narara.superboard.boardmember.interfaces.dto.MemberCollectionResponseDto;
import com.narara.superboard.boardmember.interfaces.dto.MemberResponseDto;
import com.narara.superboard.common.constant.enums.Authority;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.workspace.entity.WorkSpace;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceListResponseDto;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceNameHolder;
import com.narara.superboard.workspace.service.validator.WorkSpaceValidator;
import com.narara.superboard.workspacemember.entity.WorkSpaceMember;
import com.narara.superboard.workspacemember.infrastructure.WorkSpaceMemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("워크스페이스의 멤버에 대한 단위 테스트")
class WorkSpaceMemberServiceImplTest implements MockSuperBoardUnitTests {

    @InjectMocks
    private WorkSpaceMemberServiceImpl workSpaceMemberService;

    @Mock
    private WorkSpaceMemberRepository workSpaceMemberRepository;
    @Mock
    private WorkSpaceValidator workSpaceValidator;


    @Test
    @DisplayName("멤버의 워크스페이스 리스트 조회 성공 테스트")
    void testGetMemberWorkspaceListSuccess() {
        // given
        Member member = new Member(1L , "시현", "sisi@naver.com");

        // Mock된 WorkSpace 데이터 생성
        WorkSpace mockWorkSpace1 = new WorkSpace(1L, "워크스페이스 1", 1L);
        WorkSpace mockWorkSpace2 = new WorkSpace(2L, "워크스페이스 2", 1L);

        // Mock된 WorkSpaceMember 데이터 생성
        WorkSpaceMember mockWorkSpaceMember1 = new WorkSpaceMember(mockWorkSpace1);
        WorkSpaceMember mockWorkSpaceMember2 = new WorkSpaceMember(mockWorkSpace2);

        // Mock된 WorkSpaceMember 리스트 생성
        List<WorkSpaceMember> mockWorkSpaceMemberList = List.of(mockWorkSpaceMember1, mockWorkSpaceMember2);

        // workSpaceMemberRepository의 동작 설정
        when(workSpaceMemberRepository.findAllByMember(member)).thenReturn(mockWorkSpaceMemberList);

        // when
        WorkSpaceListResponseDto result = workSpaceMemberService.getMemberWorkspaceList(member);

        // then
        assertEquals(2, result.workSpaceResponseDtoList().size());
        assertEquals("워크스페이스 1", result.workSpaceResponseDtoList().get(0).name());
        assertEquals("워크스페이스 2", result.workSpaceResponseDtoList().get(1).name());

        // workSpaceValidator의 validateNameIsPresent 메서드가 호출되었는지 확인
        verify(workSpaceValidator, times(2)).validateNameIsPresent(any(WorkSpaceNameHolder.class));

        // workSpaceMemberRepository가 정확히 한 번 호출되었는지 확인
        verify(workSpaceMemberRepository, times(1)).findAllByMember(member);
    }

    @Test
    @DisplayName("워크스페이스에 권한이 있는 멤버 리스트 조회 성공 테스트")
    void testGetWorkspaceMemberCollectionResponseDtoSuccess() {
        // given
        Long workSpaceId = 1L;

        // Mock된 Member와 WorkSpaceMember 데이터 설정
        Member mockMember1 = new Member(1L, "닉네임1", "user1@example.com", "","http://profile1.img");
        Member mockMember2 = new Member(2L, "닉네임2", "user2@example.com", "","http://profile2.img");


        WorkSpaceMember mockWorkSpaceMember1 = new WorkSpaceMember(mockMember1, Authority.ADMIN);
        WorkSpaceMember mockWorkSpaceMember2 = new WorkSpaceMember(mockMember2, Authority.MEMBER);

        List<WorkSpaceMember> mockWorkSpaceMemberList = List.of(mockWorkSpaceMember1, mockWorkSpaceMember2);

        // when: workSpaceMemberRepository의 동작을 정의
        when(workSpaceMemberRepository.findAllByWorkSpaceId(workSpaceId)).thenReturn(mockWorkSpaceMemberList);

        // when: 서비스 메서드 호출
        MemberCollectionResponseDto result = workSpaceMemberService.getWorkspaceMemberCollectionResponseDto(workSpaceId);

        // then: 결과 검증
        assertEquals(2, result.memberListResponse().size());

        // 첫 번째 멤버에 대한 검증
        MemberResponseDto firstMember = result.memberListResponse().get(0);
        assertEquals(1L, firstMember.memberId());
        assertEquals("user1@example.com", firstMember.memberEmail());
        assertEquals("닉네임1", firstMember.memberNickname());
        assertEquals("http://profile1.img", firstMember.memberProfileImgUrl());
        assertEquals("ADMIN", firstMember.authority());

        // 두 번째 멤버에 대한 검증
        MemberResponseDto secondMember = result.memberListResponse().get(1);
        assertEquals(2L, secondMember.memberId());
        assertEquals("user2@example.com", secondMember.memberEmail());
        assertEquals("닉네임2", secondMember.memberNickname());
        assertEquals("http://profile2.img", secondMember.memberProfileImgUrl());
        assertEquals("MEMBER", secondMember.authority());

        // verify: workSpaceMemberRepository가 정확히 한 번 호출되었는지 확인
        verify(workSpaceMemberRepository, times(1)).findAllByWorkSpaceId(workSpaceId);
    }
}
