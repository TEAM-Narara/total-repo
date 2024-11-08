package com.narara.superboard.common.entity;

import com.narara.superboard.member.entity.Member;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@ToString
public class CustomUserDetails implements UserDetails {
    @Getter
    private final Long userId; // 사용자 ID
    private final Collection<? extends GrantedAuthority> authorities; // 권한 목록
    @Getter
    private final Member member; // Member 객체를 포함

    public CustomUserDetails(Member member, Long userId, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.authorities = authorities;
        this.member = member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return String.valueOf(userId);
    }
}
