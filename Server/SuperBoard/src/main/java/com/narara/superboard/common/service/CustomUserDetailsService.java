package com.narara.superboard.common.service;

import com.narara.superboard.boardmember.entity.BoardMember;
import com.narara.superboard.common.entity.CustomUserDetails;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.infrastructure.MemberRepository;
import com.narara.superboard.workspacemember.entity.WorkSpaceMember;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// TODO : 캐싱으로 성능 업데이트 하기
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository; // 사용자 정보를 가져오는 repository

    /**
     * 사용자 ID를 기반으로 UserDetails를 반환하는 메서드
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            System.out.println("customUserDetailsService loadUserByUsername : " + username);
            long userId = Long.parseLong(username); // username 대신 userId를 사용
            System.out.println(username);
            // 사용자 ID로 사용자 정보를 조회
            Member member = memberRepository.findByIdAndIsDeletedFalse(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("아이디로 회원을 찾을 수 없습니다.: " + userId));

            // 사용자 권한을 가져오는 로직
            List<GrantedAuthority> authorities = getUserAuthorities(member);

            // CustomUserDetails 객체 생성 후 반환
            return new CustomUserDetails(member, member.getId(), authorities);

        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Invalid user ID format: " + username);
        }
    }

    /**
     * 사용자의 권한(워크스페이스, 보드) 정보를 조회하여 GrantedAuthority 리스트를 반환
     *
     * @param member
     * @return
     */
    private List<GrantedAuthority> getUserAuthorities(Member member) {
        // 기존 코드
//        // 워크스페이스와 보드에서의 권한을 가져옴
//        List<GrantedAuthority> workspaceAuthorities = member.getWorkspaceMemberList().stream()
//                .map(role -> new SimpleGrantedAuthority("ROLE_WORKSPACE_" + role.getAuthority() + "_ID_" + role.getWorkSpace().getId()))
//                .collect(Collectors.toList());
//
//        List<GrantedAuthority> boardAuthorities = member.getBoardMemberList().stream()
//                .map(role -> new SimpleGrantedAuthority("ROLE_BOARD_" + role.getAuthority() + "_ID_" + role.getBoard().getId()))
//                .collect(Collectors.toList());
//
//        // 두 리스트를 결합하여 반환
//        workspaceAuthorities.addAll(boardAuthorities);
//        return null;

        // 그냥 비어있는건 아닌 듯 해서 넣은 코드
        List<GrantedAuthority> authorities = new ArrayList<>();

        // 예시: 기본적으로 ROLE_USER 권한 추가
        authorities.add(new SimpleGrantedAuthority("ROLE_MEMBER"));
        return authorities;
    }
}
