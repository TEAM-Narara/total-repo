package com.narara.superboard.member.infrastructure;

import com.narara.superboard.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Page<Member> findByNicknameContainingIgnoreCaseOrEmailContainingIgnoreCase
            (String nicknameTerm, String emailTerm, Pageable pageable);
}
