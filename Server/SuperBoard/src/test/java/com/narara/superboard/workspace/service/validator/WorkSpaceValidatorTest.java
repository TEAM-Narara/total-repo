package com.narara.superboard.workspace.service.validator;

import com.narara.superboard.MockSuperBoardUnitTests;
import com.narara.superboard.workspace.exception.WorkspaceNameNotFoundException;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceCreateRequestDto;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceUpdateRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("워크스페이스 검증 테스트")
class WorkSpaceValidatorTest implements MockSuperBoardUnitTests {

    @InjectMocks
    private WorkSpaceValidator workSpaceValidator;

    @DisplayName("생성 DTO에 이름이 없으면 에러가 발생한다.")
    @ParameterizedTest
    @CsvSource({
            ", '워크스페이스 설명'",  // 이름이 없는 경우
            "'', '워크스페이스 설명'"  // 이름이 빈 문자열인 경우
    })
    void testWorkSpaceEntityCreation(String name, String description) {
        WorkSpaceCreateRequestDto workspaceCreateDto = new WorkSpaceCreateRequestDto(name);

        assertThrows(WorkspaceNameNotFoundException.class, () -> workSpaceValidator.validateNameIsPresent(workspaceCreateDto.name()));
    }

    @DisplayName("수정 DTO에 이름이 없으면 에러가 발생한다.")
    @ParameterizedTest
    @CsvSource({
            ", '워크스페이스 설명'",  // 이름이 없는 경우
            "'', '워크스페이스 설명'"  // 이름이 빈 문자열인 경우
    })
    void testWorkSpaceEntityUpdate(String name, String description) {
        WorkSpaceUpdateRequestDto workspaceUpdateRequestDto = new WorkSpaceUpdateRequestDto(name);

        assertThrows(WorkspaceNameNotFoundException.class, () -> workSpaceValidator.validateNameIsPresent(workspaceUpdateRequestDto.name()));
    }
}