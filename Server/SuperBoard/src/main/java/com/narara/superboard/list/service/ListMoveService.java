package com.narara.superboard.list.service;

import com.narara.superboard.list.interfaces.dto.ListMoveCollectionRequest;
import com.narara.superboard.list.interfaces.dto.ListMoveResult;
import com.narara.superboard.member.entity.Member;

public interface ListMoveService {
    ListMoveResult moveListToTop(Member member, Long listId);
    ListMoveResult moveListToBottom(Member member, Long listId);
    ListMoveResult moveListBetween(Member member, Long listId, Long previousListId, Long nextListId);
    ListMoveResult moveListVersion1(Member member, Long listId, Long myOrder);
    ListMoveResult moveListVersion2(Member member, ListMoveCollectionRequest listMoveCollectionRequest);
}
