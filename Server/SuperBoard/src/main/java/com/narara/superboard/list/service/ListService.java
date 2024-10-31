package com.narara.superboard.list.service;

import com.narara.superboard.card.CardAction;
import com.narara.superboard.list.entity.List;
import com.narara.superboard.list.interfaces.dto.ListCreateRequestDto;
import com.narara.superboard.list.interfaces.dto.ListUpdateRequestDto;
import com.narara.superboard.member.entity.Member;

public interface ListService {
    List createList(ListCreateRequestDto listCreateRequestDto);
    List updateList(Long listId, ListUpdateRequestDto listUpdateRequestDto);
    List getList(Long listId);
    List changeListIsArchived(Long listId);
    java.util.List<List> getArchivedList(Long listId);
    void checkBoardMember(List list, Member member, CardAction action);

}
