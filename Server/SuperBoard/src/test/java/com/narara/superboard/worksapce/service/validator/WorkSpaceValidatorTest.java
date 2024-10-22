package com.narara.superboard.worksapce.service.validator;

import com.narara.superboard.common.exception.WorkspaceNameNotFoundException;
import com.narara.superboard.worksapce.interfaces.dto.WorkspaceRequestCreateDto;
import com.narara.superboard.worksapce.interfaces.dto.WorkspaceUpdateRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("워크스페이스 검증 테스트")
class WorkSpaceValidatorTest {

    @InjectMocks
    private WorkSpaceValidator workSpaceValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Mockito 초기화
    }

    @DisplayName("생성 DTO에 이름이 없으면 에러가 발생한다.")
    @ParameterizedTest
    @CsvSource({
            ", '워크스페이스 설명'",  // 이름이 없는 경우
            "'', '워크스페이스 설명'"  // 이름이 빈 문자열인 경우
    })
    void testWorkSpaceEntityCreation(String name, String description) {
        WorkspaceRequestCreateDto workspaceCreateDto = new WorkspaceRequestCreateDto(name);

        assertThrows(WorkspaceNameNotFoundException.class, () -> workSpaceValidator.validateNameIsPresent(workspaceCreateDto));
    }

    @DisplayName("수정 DTO에 이름이 없으면 에러가 발생한다.")
    @ParameterizedTest
    @CsvSource({
            ", '워크스페이스 설명'",  // 이름이 없는 경우
            "'', '워크스페이스 설명'"  // 이름이 빈 문자열인 경우
    })
    void testWorkSpaceEntityUpdate(String name, String description) {
        WorkspaceUpdateRequestDto workspaceUpdateRequestDto = new WorkspaceUpdateRequestDto(name);

        assertThrows(WorkspaceNameNotFoundException.class, () -> workSpaceValidator.validateNameIsPresent(workspaceUpdateRequestDto));
    }
}