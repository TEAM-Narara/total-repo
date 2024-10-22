package com.narara.superboard.worksapce.service;


import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.worksapce.entity.WorkSpace;
import com.narara.superboard.worksapce.infrastructure.WorkSpaceRepository;
import com.narara.superboard.worksapce.interfaces.dto.WorkspaceRequestCreateDto;
import com.narara.superboard.worksapce.interfaces.dto.WorkspaceUpdateRequestDto;
import com.narara.superboard.worksapce.service.validator.WorkSpaceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("워크스페이스 서비스 테스트")
class WorkSpaceServiceTest {

    // 실제 테스트 대상 객체를 생성하고 초기화할 때 사용
    @InjectMocks
    private WorkSpaceServiceImpl workSpaceService; // 테스트 대상 클래스

    @Mock
    private WorkSpaceRepository workSpaceRepository; // 의존성을 Mocking

    @Mock
    private WorkSpaceValidator workSpaceValidator; // 의존성을 Mocking

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
        WorkspaceRequestCreateDto workspaceCreateDto = new WorkspaceRequestCreateDto(name, description);

        // when
        workSpaceService.createWorkSpace(workspaceCreateDto);

        // then
        // 메서드가 한 번 호출되었는지 확인
        verify(workSpaceValidator, times(1)).validateNameIsPresent(workspaceCreateDto);
        verify(workSpaceRepository, times(1)).save(any());
    }

    @DisplayName("workspace 이름과 설명 수정 성공")
    @Test
    void updateWorkspaceSuccess() {
        // Given
        Long workspaceId = 1L;
        String newName = "새로운 워크스페이스";
        String newDescription = "새로운 설명";

        WorkSpace existingWorkspace = WorkSpace.builder()
                .id(workspaceId)
                .name("기존 워크스페이스")
                .description("기존 설명")
                .build();

        WorkspaceUpdateRequestDto updateRequest = WorkspaceUpdateRequestDto.builder()
                .name(newName)
                .description(newDescription)
                .build();


//        즉, 기존의 워크스페이스가 존재한다는 시나리오를 시뮬레이션합니다.
        when(workSpaceRepository.findById(workspaceId))
                .thenReturn(Optional.of(existingWorkspace));

        // When
        WorkSpace result = workSpaceService.updateWorkSpace(workspaceId, updateRequest);

        // Then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(newName, result.getName()),
                () -> assertEquals(newDescription, result.getDescription()),
                () -> verify(workSpaceRepository).findById(workspaceId)
        );
    }

    @DisplayName("워크스페이스를 찾을 수 없는 경우 예외 발생")
    @Test
    void testGetWorkSpace_NotFound() {
        // given
        Long workSpaceId = 1L;
        when(workSpaceRepository.findById(workSpaceId)).thenReturn(Optional.empty());  // 빈 Optional을 반환하도록 설정

        // when & then
        NotFoundEntityException exception = assertThrows(NotFoundEntityException.class,
                () -> workSpaceService.getWorkSpace(workSpaceId));  // 예외가 발생하는지 확인

        // 추가 검증: 예외 객체에 담긴 ID와 엔티티 타입이 정확한지 확인
        assertEquals(workSpaceId, exception.getId());  // 예외에 저장된 ID가 일치하는지 확인
        assertEquals("WorkSpace", exception.getEntity());  // 예외에 저장된 엔티티 타입이 일치하는지 확인

        verify(workSpaceRepository, times(1)).findById(workSpaceId);  // findById가 한 번 호출되었는지 확인
    }


    // WorkSpace 객체를 실제로 만들어서 테스트하기 위한 데이터 제공 메서드
    static Stream<WorkSpace> provideWorkSpaces() {
        return Stream.of(
                new WorkSpace(1L, "Workspace 1", "Description 1"),
                new WorkSpace(2L, "Workspace 2", "Description 2"),
                new WorkSpace(3L, "Workspace 3", "Description 3")
        );
    }

    @DisplayName("워크스페이스를 성공적으로 찾을 수 있는 경우")
    @ParameterizedTest
    @MethodSource("provideWorkSpaces")
    void testGetWorkSpace_Success(WorkSpace workSpace) {
        // given
        Long workSpaceId = workSpace.getId();  // WorkSpace의 ID
        when(workSpaceRepository.findById(workSpaceId)).thenReturn(Optional.of(workSpace));  // 정상적으로 WorkSpace가 반환되는 상황 설정

        // when
        WorkSpace result = workSpaceService.getWorkSpace(workSpaceId);  // 실제 getWorkSpace 호출

        // then
        assertNotNull(result);  // 결과가 null이 아닌지 확인
        assertEquals(workSpace, result);  // 반환된 객체가 기대한 객체와 일치하는지 확인
        verify(workSpaceRepository, times(1)).findById(workSpaceId);  // findById가 한 번 호출되었는지 검증
    }

}