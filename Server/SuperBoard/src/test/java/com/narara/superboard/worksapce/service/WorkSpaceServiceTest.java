package com.narara.superboard.worksapce.service;


import com.narara.superboard.common.exception.WorkspaceNameNotFoundException;
import com.narara.superboard.worksapce.infrastructure.WorkSpaceRepository;
import com.narara.superboard.worksapce.interfaces.dto.WorkspaceCreateDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("워크스페이스 서비스 테스트")
class WorkSpaceServiceTest {

    // 실제 테스트 대상 객체를 생성하고 초기화할 때 사용
    @InjectMocks
    private WorkSpaceService workSpaceService; // 테스트 대상 클래스

    @Mock
    private WorkSpaceRepository workspaceRepository;


    @DisplayName("워크스페이스 생성 시, 이름이 없으면 에러가 발생한다.")
    @ParameterizedTest
    @CsvSource({
            ", '워크스페이스 설명'",  // 이름이 없는 경우
            "'', '워크스페이스 설명'"  // 이름이 빈 문자열인 경우
    })
    void testWorkSpaceEntityCreation(String name, String description) {
        WorkspaceCreateDto workspaceCreateDto = new WorkspaceCreateDto(name, description);

        assertThrows(WorkspaceNameNotFoundException.class, () -> workSpaceService.createWorkSpace(workspaceCreateDto));
    }

    @DisplayName("워크스페이스가 정상적으로 생성된다.")
    @ParameterizedTest
    @CsvSource({
            "'나의 워크 스페이스', '워크스페이스 설명'",
            "'workspace', '워크스페이스 설명'"
    })
    void testSuccessfulWorkSpaceCreation(String name, String description) {
        // given
        WorkspaceCreateDto workspaceCreateDto = new WorkspaceCreateDto(name, description);

        // when
        workSpaceService.createWorkSpace(workspaceCreateDto);

        // then
        // 메서드가 한 번 호출되었는지 확인
        verify(workSpaceService, times(1)).createWorkSpace(workspaceCreateDto);
    }


    @DisplayName("워크스페이스 수정 시, 이름이 없으면 에러가 발생한다.")
    @ParameterizedTest
    @CsvSource({
            ", '워크스페이스 설명'",  // 이름이 없는 경우
            "'', '워크스페이스 설명'"  // 이름이 빈 문자열인 경우
    })
    void testUpdateWorkspaceNameError(String name, String description) {
        // given
        Long workspaceId = 1L;
        WorkspaceUpdateDto dto = new WorkspaceUpdateDto(name, description);

        // when & then
        // JUnit의 메서드로, 실제로 예외가 발생하는지 검증하는 코드
        assertThrows(WorkspaceNameNotFoundException.class, () -> {
            workSpaceService.updateWorkspace(workspaceId, dto);
        });

        // 서비스 호출 검증
        verify(workSpaceService, times(1)).updateWorkspace(workspaceId, dto);
    }
}