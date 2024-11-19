package com.narara.superboard.memberbackground.service;

import com.narara.superboard.member.entity.Member;
import com.narara.superboard.memberbackground.entity.MemberBackground;

import java.util.List;

public interface MemberBackgroundService {
    // 회원 배경 추가
    MemberBackground addMemberBackground(Long memberId,String imgUrl);
    // 회원 배경 리스트 조회
    List<MemberBackground> getAllMemberBackground(Member member);
    // 회원 배경 삭제
    void deleteMemberBackground(Member member,Long backgroundId);
}
