package com.narara.superboard.common.service;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.board.infrastructure.BoardRepository;
import com.narara.superboard.boardmember.infrastructure.BoardMemberRepository;
import com.narara.superboard.common.constant.enums.Authority;
import com.narara.superboard.common.entity.CustomUserDetails;
import com.narara.superboard.common.exception.CustomAccessDeniedException;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.infrastructure.MemberRepository;
import com.narara.superboard.reply.entity.Reply;
import com.narara.superboard.reply.infrastructure.ReplyRepository;
import com.narara.superboard.workspace.entity.WorkSpace;
import com.narara.superboard.workspace.infrastructure.WorkSpaceRepository;
import com.narara.superboard.workspacemember.infrastructure.WorkSpaceMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Spring Security에서 사용자 권한을 평가하는 로직
 */
@Slf4j
@Component // 이 클래스를 Spring의 컴포넌트로 등록
@RequiredArgsConstructor
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final WorkSpaceMemberRepository workSpaceMemberRepository;
    private final BoardMemberRepository boardMemberRepository;
    private final MemberRepository memberRepository;
    private final WorkSpaceRepository workSpaceRepository;
    private final BoardRepository boardRepository;
    private final ReplyRepository replyRepository;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return false;
    }

        /**
         * 권한 인증 로직
         * @param authentication 인증 정보
         * @param targetId 검증할 객체 (워크스페이스 ID 또는 보드 ID)
         * @param targetType 워크스페이스 or 보드
         * @param permission 검증할 권한
         * @return
         */
    // 일관성과 보안 관리가 중요한 경우
    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new CustomAccessDeniedException();
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();
        Member member = findMemberById(userId);

        return switch (targetType.toUpperCase()) {
            case "WORKSPACE" -> {
                WorkSpace workSpace = findWorkSpaceById(Long.valueOf(targetId.toString()));
                yield hasWorkspacePermission(member, workSpace, permission.toString());
            }
            case "BOARD" -> {
                Board board = findBoardById(Long.valueOf(targetId.toString()));
                yield hasBoardPermission(member, board, permission.toString());
            }
            case "REPLY" -> {
                Long replyId = Long.valueOf(targetId.toString());
                yield hasReplyPermission(member, replyId);
            }
            default -> false;
        };
    }

    private boolean hasReplyPermission(Member member, Long replyId) {
        return replyRepository.existsByMemberAndId(member, replyId);
    }

    private boolean hasWorkspacePermission(Member member, WorkSpace workSpace, String permission) {
        return hasPermission(member, workSpace, permission,
                workSpaceMemberRepository::existsByMemberAndWorkSpaceAndAuthority);
    }

    private boolean hasBoardPermission(Member member, Board board, String permission) {
        return hasPermission(member, board, permission,
                boardMemberRepository::existsByMemberAndBoardAndAuthority);
    }

    private boolean hasPermission(Member member, WorkSpace workSpace, String permission, PermissionCheckFunction<WorkSpace> checkFunction) {
        return evaluatePermission(member, workSpace, permission, checkFunction);
    }

    private boolean hasPermission(Member member, Board board, String permission, PermissionCheckFunction<Board> checkFunction) {
        return evaluatePermission(member, board, permission, checkFunction);
    }

    private <T> boolean evaluatePermission(Member member, T target, String permission, PermissionCheckFunction<T> checkFunction) {
        try {
            return switch (permission.toUpperCase()) {
                case "ADMIN" -> checkFunction.check(member, target, Authority.ADMIN);
                case "MEMBER" -> checkFunction.check(member, target, Authority.MEMBER) || checkFunction.check(member, target, Authority.ADMIN);
                default -> throw new CustomAccessDeniedException();
            };
        } catch (Exception e) {
            // 예외 처리 로직 추가 (예: 로그 기록)
            return false; // 권한 확인 중 오류 발생 시 false 반환
        }
    }

    @FunctionalInterface
    private interface PermissionCheckFunction<T> {
        boolean check(Member member, T target, Authority authority);
    }

    private Member findMemberById(Long userId) {
        return memberRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with ID: " + userId));
    }

    private WorkSpace findWorkSpaceById(Long workspaceId) {
        return workSpaceRepository.findByIdAndIsDeletedFalse(workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("WorkSpace not found with ID: " + workspaceId));
    }

    private Board findBoardById(Long boardId) {
        return boardRepository.findByIdAndIsDeletedFalse(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found with ID: " + boardId));
    }

    private Reply findReplyById(Long aLong) {
        return replyRepository.findById(aLong)
                .orElseThrow(() -> new NotFoundEntityException(aLong, "댓글"));
    }
}
