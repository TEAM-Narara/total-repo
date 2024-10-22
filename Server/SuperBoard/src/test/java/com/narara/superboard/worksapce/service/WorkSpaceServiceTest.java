package com.narara.superboard.worksapce.service;


import com.narara.superboard.common.exception.WorkspaceNameNotFoundException;
import com.narara.superboard.worksapce.interfaces.dto.CreateWorkspaceDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@DisplayName("워크스페이스 서비스 테스트")
class WorkSpaceServiceTest {

    // 실제 테스트 대상 객체를 생성하고 초기화할 때 사용
    @InjectMocks
    private WorkSpaceService workSpaceService; // 테스트 대상 클래스

    @DisplayName("워크스페이스 생성 시, 이름이 없으면 에러가 발생한다.")
    @ParameterizedTest
    @CsvSource({
            ", '워크스페이스 설명'",  // 이름이 없는 경우
            "'', '워크스페이스 설명'"  // 이름이 빈 문자열인 경우
    })
    void testWorkSpaceEntityCreation(String name, String description) {
        CreateWorkspaceDto createWorkspaceDto = new CreateWorkspaceDto(name, description);

        assertThrows(WorkspaceNameNotFoundException.class, () -> workSpaceService.createWorkSpace(createWorkspaceDto));
    }

    @DisplayName("워크스페이스가 정상적으로 생성된다.")
    @ParameterizedTest
    @CsvSource({
            "'나의 워크 스페이스', '워크스페이스 설명'",
            "'workspace', '워크스페이스 설명'"
    })
    void testSuccessfulWorkSpaceCreation(String name, String description) {
        // given
        CreateWorkspaceDto createWorkspaceDto = new CreateWorkspaceDto(name, description);

        // when
        workSpaceService.createWorkSpace(createWorkspaceDto);

        // then
        // 메서드가 한 번 호출되었는지 확인
        verify(workSpaceService, times(1)).createWorkSpace(createWorkspaceDto);
    }
}