package com.narara.superboard.common.service;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.board.infrastrucutre.BoardRepository;
import com.narara.superboard.boardmember.infrastructure.BoardMemberRepository;
import com.narara.superboard.common.entity.CustomUserDetails;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.infrastructure.MemberRepository;
import com.narara.superboard.workspace.entity.WorkSpace;
import com.narara.superboard.workspace.infrastructure.WorkSpaceRepository;
import com.narara.superboard.workspacemember.infrastructure.WorkSpaceMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Spring Security에서 사용자 권한을 평가하는 로직
 */
@Component // 이 클래스를 Spring의 컴포넌트로 등록
@RequiredArgsConstructor
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final WorkSpaceMemberRepository workSpaceMemberRepository;
    private final BoardMemberRepository boardMemberRepository;
    private final MemberRepository memberRepository;
    private final WorkSpaceRepository workSpaceRepository;
    private final BoardRepository boardRepository;

    /**
     * 권한 인증 로직
     * @param authentication 인증 정보
     * @param targetDomainObject 검증할 객체 (워크스페이스 ID 또는 보드 ID)
     * @param permission 검증할 권한
     * @return 권한 여부
     */
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return false; // 인증이 없거나 CustomUserDetails가 아닐 경우 false 반환
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        // 워크스페이스 ID가 targetDomainObject에 포함되어야 함
        if (targetDomainObject instanceof Long workspaceId) {
            return hasWorkspacePermission(userId, workspaceId, permission.toString());
        }

        // 보드 ID가 targetDomainObject에 포함되어야 함
        if (targetDomainObject instanceof Long boardId) {
            return hasBoardPermission(userId, boardId, permission.toString());
        }

        return false; // targetDomainObject가 워크스페이스나 보드 ID가 아닐 경우 false 반환
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }

    private boolean hasWorkspacePermission(Long userId, Long workspaceId, String permission) {
        try {
            // Member와 WorkSpace 인스턴스 조회
            Member member = findMemberById(userId);
            WorkSpace workSpace = findWorkSpaceById(workspaceId);

            return switch (permission.toUpperCase()) {
                case "ADMIN" -> workSpaceMemberRepository.existsByMemberAndWorkSpaceAndAuthority(member, workSpace, "ADMIN");
                case "MEMBER" -> workSpaceMemberRepository.existsByMemberAndWorkSpaceAndAuthority(member, workSpace, "MEMBER");
                default -> false; // 해당 권한이 없는 경우
            };
        } catch (Exception e) {
            // 예외 처리 로직 추가 (예: 로그 기록)
            return false; // 권한 확인 중 오류 발생 시 false 반환
        }
    }

    private boolean hasBoardPermission(Long userId, Long boardId, String permission) {
        try {
            // Member와 WorkSpace 인스턴스 조회
            Member member = findMemberById(userId);
            Board board = findBoardById(boardId);

            return switch (permission.toUpperCase()) {
                case "ADMIN" -> boardMemberRepository.existsByMemberAndBoardAndAuthority(member, board, "ADMIN");
                case "MEMBER" -> boardMemberRepository.existsByMemberAndBoardAndAuthority(member, board, "MEMBER");
                default -> false; // 해당 권한이 없는 경우
            };
        } catch (Exception e) {
            // 예외 처리 로직 추가 (예: 로그 기록)
            return false; // 권한 확인 중 오류 발생 시 false 반환
        }
    }

    private Member findMemberById(Long userId) {
        return memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with ID: " + userId));
    }

    private WorkSpace findWorkSpaceById(Long workspaceId) {
        return workSpaceRepository.findById(workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("WorkSpace not found with ID: " + workspaceId));
    }

    private Board findBoardById(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found with ID: " + boardId));
    }
}
