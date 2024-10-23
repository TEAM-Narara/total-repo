package com.narara.superboard.workspace.service;


import com.narara.superboard.board.interfaces.dto.BoardCollectionResponseDto;
import com.narara.superboard.board.interfaces.dto.BoardDetailResponseDto;
import com.narara.superboard.board.service.BoardService;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.workspace.entity.WorkSpace;
import com.narara.superboard.workspace.infrastructure.WorkSpaceRepository;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceDetailResponseDto;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceRequestCreateDto;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceUpdateRequestDto;
import com.narara.superboard.workspace.service.validator.WorkSpaceValidator;
import com.narara.superboard.workspacemember.interfaces.dto.WorkspaceMemberCollectionResponseDto;
import com.narara.superboard.workspacemember.interfaces.dto.WorkSpaceMemberDetailResponseDto;
import com.narara.superboard.workspacemember.service.WorkSpaceMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.util.List;
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
    private BoardService boardService;

    @Mock
    private WorkSpaceMemberService workSpaceMemberService;

    @Mock
    private WorkSpaceRepository workSpaceRepository; // 의존성을 Mocking

    @Mock
    private WorkSpaceValidator workSpaceValidator; // 의존성을 Mocking

    @BeforeEach
    void setUp() {
        // Mockito가 Mock 객체를 초기화
        MockitoAnnotations.openMocks(this);
    }


    @DisplayName("워크스페이스가 생성 성공 테스트")
    @ParameterizedTest
    @CsvSource({
            "'나의 워크 스페이스', '워크스페이스 설명'",
            "'workspace', '워크스페이스 설명'"
    })
    void testSuccessfulWorkSpaceCreation(String name
//                                         String description
    ) {
        // given
        WorkSpaceRequestCreateDto workspaceCreateDto = new WorkSpaceRequestCreateDto(name);

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
//        String newDescription = "새로운 설명";

        WorkSpace existingWorkspace = WorkSpace.builder()
                .id(workspaceId)
                .name("기존 워크스페이스")
//                .description("기존 설명")
                .build();

        WorkSpaceUpdateRequestDto updateRequest = WorkSpaceUpdateRequestDto.builder()
                .name(newName)
//                .description(newDescription)
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
//                () -> assertEquals(newDescription, result.getDescription()),
                () -> verify(workSpaceRepository).findById(workspaceId)
        );
    }

    @DisplayName("워크스페이스 삭제 성공 테스트")
    @Test
    void deleteWorkSpace_Success() {
        // Given
        Long workspaceId = 1L;

        // 가정: 이 ID에 대한 워크스페이스가 존재함
        WorkSpace mockWorkSpace = WorkSpace.builder()
                .id(workspaceId)
                .name("my Workspace")
//                .description("my Description")
                .build();

        // getWorkSpace 메서드가 워크스페이스를 반환하도록 설정
        when(workSpaceRepository.findById(workspaceId)).thenReturn(Optional.of(mockWorkSpace));

        // When
        workSpaceService.deleteWorkSpace(workspaceId);  // deleteWorkSpace 메서드 호출

        // Then
        verify(workSpaceRepository, times(1)).findById(workspaceId);
        verify(workSpaceRepository, times(1)).delete(mockWorkSpace);
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
                new WorkSpace(1L, "Workspace 1"
//                        , "Description 1"
                ),
                new WorkSpace(2L, "Workspace 2"
//                        , "Description 2"
                ),
                new WorkSpace(3L, "Workspace 3"
//                        , "Description 3"
                )
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


    @Test
    @DisplayName("워크스페이스 상세 조회 성공 테스트")
    void testGetWorkspaceDetailSuccess() {
        // given
        Long workspaceId = 1L;

        WorkSpace mockWorkSpace =  new WorkSpace(workspaceId, "시현의 워크스페이스");
        BoardCollectionResponseDto mockBoardCollectionResponseDto = createMockBoardCollection();
        WorkspaceMemberCollectionResponseDto mockMemberCollectionResponseDto = createMockMemberCollection();

        // Mocking: repository, boardService, workSpaceMemberService, workSpaceValidator 동작 설정
        mockDependencies(workspaceId, mockWorkSpace, mockBoardCollectionResponseDto, mockMemberCollectionResponseDto);

        // when
        WorkSpaceDetailResponseDto result = workSpaceService.getWorkspaceDetail(workspaceId);

        // then
        assertWorkspaceDetail(result, mockWorkSpace, mockBoardCollectionResponseDto, mockMemberCollectionResponseDto);
    }

    private BoardCollectionResponseDto createMockBoardCollection() {
        return BoardCollectionResponseDto.builder()
                .boardDetailResponseDtoList(List.of(
                        BoardDetailResponseDto.builder()
                                .id(1L)
                                .name("나의 보드1")
                                .backgroundType("COLOR")
                                .backgroundValue("#fffffff")
                                .build(),
                        BoardDetailResponseDto.builder()
                                .id(2L)
                                .name("나의 보드2")
                                .backgroundType("IMAGE")
                                .backgroundValue("https!!~~~")
                                .build()
                ))
                .build();
    }

    private WorkspaceMemberCollectionResponseDto createMockMemberCollection() {
        return WorkspaceMemberCollectionResponseDto.builder()
                .workspaceMemberList(List.of(
                        WorkSpaceMemberDetailResponseDto.builder()
                                .memberId(1L)
                                .memberEmail("asdf@eawefsdz")
                                .memberNickname("조시현")
                                .memberProfileImgUrl("http~~")
                                .authority("ADMIN")
                                .build(),
                        WorkSpaceMemberDetailResponseDto.builder()
                                .memberId(2L)
                                .memberEmail("qwer@eawefsdz")
                                .memberNickname("주효림")
                                .memberProfileImgUrl("http~~")
                                .authority("MEMBER")
                                .build()
                ))
                .build();
    }

    private void mockDependencies(Long workspaceId, WorkSpace mockWorkSpace,
                                  BoardCollectionResponseDto mockBoardCollectionResponseDto,
                                  WorkspaceMemberCollectionResponseDto mockMemberCollectionResponseDto) {
        when(workSpaceRepository.findById(workspaceId)).thenReturn(Optional.of(mockWorkSpace));
        when(boardService.getBoardCollectionResponseDto(workspaceId)).thenReturn(mockBoardCollectionResponseDto);
        when(workSpaceMemberService.getWorkspaceMemberCollectionResponseDto(workspaceId)).thenReturn(mockMemberCollectionResponseDto);
    }

    private void assertWorkspaceDetail(WorkSpaceDetailResponseDto result, WorkSpace mockWorkSpace,
                                       BoardCollectionResponseDto mockBoardCollectionResponseDto,
                                       WorkspaceMemberCollectionResponseDto mockMemberCollectionResponseDto) {
        assertEquals(mockWorkSpace.getId(), result.workSpaceId());
        assertEquals(mockWorkSpace.getName(), result.name());
        assertEquals(mockBoardCollectionResponseDto, result.boardList());
        assertEquals(mockMemberCollectionResponseDto, result.workspaceMemberList());

        // workSpaceValidator의 validateNameIsPresent 메서드가 호출되었는지 확인
        verify(workSpaceValidator, times(1)).validateNameIsPresent(result);
    }


}