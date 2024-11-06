package com.narara.superboard.memberbackground.service;

import com.narara.superboard.memberbackground.entity.MemberBackground;

import java.util.List;

public interface MemberBackgroundService {
    // 회원 배경 추가
    MemberBackground addMemberBackground(Long memberId,String imgUrl);
    // 회원 배경 리스트 조회
    List<MemberBackground> getAllMemberBackground(Long memberId);
    // 회원 배경 삭제
    void deleteMemberBackground(Long memberId,Long backgroundId);
}
