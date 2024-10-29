package com.narara.superboard.cardmember.infrastructure;

import com.narara.superboard.cardmember.entity.CardMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardMemberRepository extends JpaRepository<CardMember, Long> {
}
