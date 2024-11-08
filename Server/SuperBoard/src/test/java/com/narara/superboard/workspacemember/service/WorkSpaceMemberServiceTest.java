package com.narara.superboard.workspacemember.service;

import com.narara.superboard.boardmember.interfaces.dto.MemberCollectionResponseDto;
import com.narara.superboard.boardmember.interfaces.dto.MemberResponseDto;
import com.narara.superboard.common.application.kafka.KafkaConsumerService;
import com.narara.superboard.common.constant.enums.Authority;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.infrastructure.MemberRepository;
import com.narara.superboard.workspace.entity.WorkSpace;
import com.narara.superboard.workspace.infrastructure.WorkSpaceRepository;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceListResponseDto;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceResponseDto;
import com.narara.superboard.workspace.service.validator.WorkSpaceValidator;
import com.narara.superboard.workspacemember.entity.WorkSpaceMember;
import com.narara.superboard.workspacemember.infrastructure.WorkSpaceMemberRepository;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkSpaceMemberServiceTest {

    public static final long WORKSPACE_ID_1 = 1L;
    public static final String WORKSPACE_NAME_1 = "Test WorkSpace";
    public static final long MEMBER_ID_1 = 1L;
    @InjectMocks
    private WorkSpaceMemberServiceImpl workSpaceMemberService;

    @Mock
    private WorkSpaceMemberRepository workSpaceMemberRepository;

    @Mock
    private WorkSpaceRepository workSpaceRepository;

    @Mock
    private WorkSpaceValidator workSpaceValidator;

    @Mock
    private KafkaConsumerService kafkaConsumerService;

    @Mock
    private MemberRepository memberRepository;

    private WorkSpace workSpace;
    private Member member;
    private WorkSpaceMember workSpaceMember;

    @BeforeEach
    void setUp() {
        workSpace = WorkSpace.builder()
                .id(WORKSPACE_ID_1)
                .name(WORKSPACE_NAME_1)
                .build();

        member = Member.builder()
                .id(MEMBER_ID_1)
                .email("test@test.com")
                .nickname("tester")
                .profileImgUrl("http://test.com/img.jpg")
                .build();

        workSpaceMember = WorkSpaceMember.builder()
                .workSpace(workSpace)
                .member(member)
                .authority(Authority.MEMBER)
                .build();
    }

    @Test
    @DisplayName("워크스페이스 멤버 목록 조회 성공")
    void getWorkspaceMemberCollectionResponseDto_Success() {
        // given
        given(workSpaceMemberRepository.findAllByWorkSpaceId(WORKSPACE_ID_1))
                .willReturn(Arrays.asList(workSpaceMember));

        // when
        MemberCollectionResponseDto result =
                workSpaceMemberService.getWorkspaceMemberCollectionResponseDto(WORKSPACE_ID_1);

        // then
        assertThat(result.memberListResponse()).hasSize(1);
        MemberResponseDto dto = result.memberListResponse().get(0);
        assertThat(dto.memberEmail()).isEqualTo("test@test.com");
        assertThat(dto.memberNickname()).isEqualTo("tester");
        assertThat(dto.authority()).isEqualTo("MEMBER");
    }

    @Test
    @DisplayName("멤버의 워크스페이스 목록 조회 성공")
    void getMemberWorkspaceList_Success() {
        // given
        given(workSpaceMemberRepository.findAllByMember(member))
                .willReturn(Arrays.asList(workSpaceMember));

        // when
        WorkSpaceListResponseDto result = workSpaceMemberService.getMemberWorkspaceList(member);

        // then
        assertThat(result.workSpaceResponseDtoList()).hasSize(1);
        WorkSpaceResponseDto dto = result.workSpaceResponseDtoList().get(0);
        assertThat(dto.name()).isEqualTo("Test WorkSpace");
    }

    @Test
    @DisplayName("워크스페이스 멤버 권한 수정 성공")
    void editAuthority_Success() {
        // given
        given(workSpaceMemberRepository.findFirstByWorkSpaceIdAndMemberId(WORKSPACE_ID_1, MEMBER_ID_1))
                .willReturn(Optional.of(workSpaceMember));

        // when
        WorkSpaceMember result = workSpaceMemberService.editAuthority(MEMBER_ID_1, WORKSPACE_ID_1, Authority.ADMIN);

        // then
        assertThat(result.getAuthority()).isEqualTo(Authority.ADMIN);
    }

    @Test
    @DisplayName("워크스페이스 멤버 추가 성공")
    void addMember_Success() {
        // given
        given(workSpaceRepository.findByIdAndIsDeletedFalse(1L)).willReturn(Optional.of(workSpace));
        given(memberRepository.findByIdAndIsDeletedFalse(1L)).willReturn(Optional.of(member));
        given(workSpaceMemberRepository.findFirstByWorkSpaceIdAndMemberId(WORKSPACE_ID_1, MEMBER_ID_1))
                .willReturn(Optional.empty());
        given(workSpaceMemberRepository.save(any(WorkSpaceMember.class)))
                .willReturn(workSpaceMember);

        // when
        WorkSpaceMember result = workSpaceMemberService.addMember(WORKSPACE_ID_1, MEMBER_ID_1, Authority.MEMBER);

        // then
        assertThat(result.getMember().getId()).isEqualTo(MEMBER_ID_1);
        assertThat(result.getWorkSpace().getId()).isEqualTo(WORKSPACE_ID_1);
        assertThat(result.getAuthority()).isEqualTo(Authority.MEMBER);
    }

    @Test
    @DisplayName("워크스페이스 멤버 삭제 성공")
    void deleteMember_Success() {
        // given
        given(workSpaceMemberRepository.findFirstByWorkSpaceIdAndMemberId(WORKSPACE_ID_1, MEMBER_ID_1))
                .willReturn(Optional.of(workSpaceMember));

        when(workSpaceMemberRepository.existsByWorkSpaceAndIsDeletedIsFalse(any())).thenReturn(true);

        // when
        WorkSpaceMember result = workSpaceMemberService.deleteMember(WORKSPACE_ID_1, MEMBER_ID_1);

        // then
        assertThat(result.getIsDeleted()).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 워크스페이스 멤버 조회시 예외 발생")
    void getWorkSpaceMember_NotFound() {
        // given
        given(workSpaceMemberRepository.findFirstByWorkSpaceIdAndMemberId(WORKSPACE_ID_1, MEMBER_ID_1))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> workSpaceMemberService.deleteMember(WORKSPACE_ID_1, MEMBER_ID_1))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("찾을 수 없습니다");
    }

    @Test
    @DisplayName("이미 존재하는 워크스페이스 멤버 추가시 기존 멤버 반환")
    void addMember_AlreadyExists() {
        // given
        given(workSpaceRepository.findByIdAndIsDeletedFalse(1L)).willReturn(Optional.of(workSpace));
        given(memberRepository.findByIdAndIsDeletedFalse(1L)).willReturn(Optional.of(member));
        given(workSpaceMemberRepository.findFirstByWorkSpaceIdAndMemberId(WORKSPACE_ID_1, MEMBER_ID_1))
                .willReturn(Optional.of(workSpaceMember));

        // when
        WorkSpaceMember result = workSpaceMemberService.addMember(WORKSPACE_ID_1, MEMBER_ID_1, Authority.MEMBER);

        // then
        assertThat(result).isEqualTo(workSpaceMember);
    }
}
