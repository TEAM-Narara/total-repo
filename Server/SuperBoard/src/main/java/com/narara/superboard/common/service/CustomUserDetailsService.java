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
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        long userId;
        try {
            userId = Long.parseLong(username); // username 대신 userId를 사용
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Invalid user ID format: " + username);
        }
        // 사용자 ID로 사용자 정보를 조회
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

        // 사용자 권한을 가져오는 로직
        List<GrantedAuthority> authorities = getUserAuthorities(member);

        // CustomUserDetails 객체 생성 후 반환
        return new CustomUserDetails(member, member.getId(), authorities);
    }

    /**
     * 사용자의 권한(워크스페이스, 보드) 정보를 조회하여 GrantedAuthority 리스트를 반환
     * @param member
     * @return
     */
    private List<GrantedAuthority> getUserAuthorities(Member member) {

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

        List<GrantedAuthority> authorities = new ArrayList<>();

        // 예시: 기본적으로 ROLE_USER 권한 추가
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

//        // 필요한 경우 추가 권한들을 불러오고, authorities 리스트에 추가합니다.
//        // 예: 워크스페이스 및 보드 권한 설정 로직 (해당 권한 정보가 Member 객체에 포함되어 있는 경우)
//        if (member.getWorkspaceMemberList() != null) {
//            authorities.addAll(member.getWorkspaceMemberList().stream()
//                    .map(role -> new SimpleGrantedAuthority("ROLE_WORKSPACE_" + role.getAuthority() + "_ID_" + role.getWorkSpace().getId()))
//                    .toList());
//        }
//
//        if (member.getBoardMemberList() != null) {
//            authorities.addAll(member.getBoardMemberList().stream()
//                    .map(role -> new SimpleGrantedAuthority("ROLE_BOARD_" + role.getAuthority() + "_ID_" + role.getBoard().getId()))
//                    .toList());
//        }

        return authorities;

//        return null;
    }
}
