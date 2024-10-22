package com.narara.superboard.worksapce.service;


import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.worksapce.infrastructure.WorkSpaceRepository;
import com.narara.superboard.worksapce.interfaces.dto.WorkspaceRequestCreateDto;
import com.narara.superboard.worksapce.service.validator.WorkSpaceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

}