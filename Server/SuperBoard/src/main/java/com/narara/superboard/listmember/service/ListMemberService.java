package com.narara.superboard.listmember.service;

import com.narara.superboard.member.entity.Member;

public interface ListMemberService {
    void setListMemberIsAlert(Member member, Long listId);
}
