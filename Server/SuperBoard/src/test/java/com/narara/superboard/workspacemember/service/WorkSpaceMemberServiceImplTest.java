package com.narara.superboard.workspacemember.service;

import com.narara.superboard.member.entity.Member;
import com.narara.superboard.workspace.entity.WorkSpace;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceListResponseDto;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceResponseDto;
import com.narara.superboard.workspace.service.validator.WorkSpaceValidator;
import com.narara.superboard.workspacemember.entity.WorkSpaceMember;
import com.narara.superboard.workspacemember.infrastructure.WorkSpaceMemberRepository;
import com.narara.superboard.workspacemember.interfaces.dto.WorkSpaceMemberDetailResponseDto;
import com.narara.superboard.workspacemember.interfaces.dto.WorkspaceMemberCollectionResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("WorkSpaceMemberServiceImpl 대한 Test")
class WorkSpaceMemberServiceImplTest {

    @InjectMocks
    private WorkSpaceMemberServiceImpl workSpaceMemberService;

    @Mock
    private WorkSpaceMemberRepository workSpaceMemberRepository;
    @Mock
    private WorkSpaceValidator workSpaceValidator;

    @BeforeEach
    void setUp() {
        // Mockito가 Mock 객체를 초기화
        MockitoAnnotations.openMocks(this);
    }


    @Test
    @DisplayName("멤버의 워크스페이스 리스트 조회 성공 테스트")
    void testGetMemberWorkspaceListSuccess() {
        // given
        Long memberId = 1L;

        // Mock된 WorkSpace 데이터 생성
        WorkSpace mockWorkSpace1 = new WorkSpace(1L, "워크스페이스 1");
        WorkSpace mockWorkSpace2 = new WorkSpace(2L, "워크스페이스 2");

        // Mock된 WorkSpaceMember 데이터 생성
        WorkSpaceMember mockWorkSpaceMember1 = new WorkSpaceMember(mockWorkSpace1);
        WorkSpaceMember mockWorkSpaceMember2 = new WorkSpaceMember(mockWorkSpace2);

        // Mock된 WorkSpaceMember 리스트 생성
        List<WorkSpaceMember> mockWorkSpaceMemberList = List.of(mockWorkSpaceMember1, mockWorkSpaceMember2);

        // workSpaceMemberRepository의 동작 설정
        when(workSpaceMemberRepository.findAllByMemberId(memberId)).thenReturn(mockWorkSpaceMemberList);

        // when
        WorkSpaceListResponseDto result = workSpaceMemberService.getMemberWorkspaceList(memberId);

        // then
        assertEquals(2, result.workSpaceResponseDtoList().size());
        assertEquals("워크스페이스 1", result.workSpaceResponseDtoList().get(0).name());
        assertEquals("워크스페이스 2", result.workSpaceResponseDtoList().get(1).name());

        // workSpaceValidator의 validateNameIsPresent 메서드가 호출되었는지 확인
        verify(workSpaceValidator, times(2)).validateNameIsPresent(any(WorkSpaceResponseDto.class));

        // workSpaceMemberRepository가 정확히 한 번 호출되었는지 확인
        verify(workSpaceMemberRepository, times(1)).findAllByMemberId(memberId);
    }


}
