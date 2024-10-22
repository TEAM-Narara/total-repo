package com.narara.superboard.worksapce.service;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.*;

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

        assertThrows(IllegalArgumentException.class, () -> workSpaceService.createWorkSpace(createWorkspaceDto));
    }

}