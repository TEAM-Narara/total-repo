package com.narara.superboard.worksapce.service;


import com.narara.superboard.common.exception.WorkspaceNameNotFoundException;
import com.narara.superboard.worksapce.infrastructure.WorkSpaceRepository;
import com.narara.superboard.worksapce.interfaces.dto.WorkspaceCreateDto;
import com.narara.superboard.worksapce.interfaces.dto.WorkspaceUpdateRequestDto;
import com.narara.superboard.worksapce.service.validator.WorkSpaceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("워크스페이스 서비스 테스트")
class WorkSpaceServiceTest {

    // 실제 테스트 대상 객체를 생성하고 초기화할 때 사용
    @InjectMocks
    private WorkSpaceServiceImpl workSpaceService; // 테스트 대상 클래스

    @BeforeEach
    void setUp() {
        // Mockito가 Mock 객체를 초기화
        MockitoAnnotations.openMocks(this);
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
}