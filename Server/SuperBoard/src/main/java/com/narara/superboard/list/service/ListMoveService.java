package com.narara.superboard.list.service;

import com.narara.superboard.list.interfaces.dto.ListMoveResult;
import com.narara.superboard.member.entity.Member;

public interface ListMoveService {
    ListMoveResult moveListToTop(Member member, Long listId);
    ListMoveResult moveListToBottom(Member member, Long listId);
    ListMoveResult moveListBetween(Member member, Long listId, Long previousListId, Long nextListId);
}
