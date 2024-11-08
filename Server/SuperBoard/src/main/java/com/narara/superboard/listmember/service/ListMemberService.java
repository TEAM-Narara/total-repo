package com.narara.superboard.listmember.service;

import com.narara.superboard.member.entity.Member;

public interface ListMemberService {
    Boolean setListMemberIsAlert(Member member, Long listId);
    Boolean getListMemberIsAlert(Member member, Long listId);
}
