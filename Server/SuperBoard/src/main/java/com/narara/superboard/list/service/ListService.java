package com.narara.superboard.list.service;

import com.narara.superboard.card.CardAction;
import com.narara.superboard.list.entity.List;
import com.narara.superboard.list.interfaces.dto.ListCreateRequestDto;
import com.narara.superboard.list.interfaces.dto.ListUpdateRequestDto;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.websocket.constant.Action;

public interface ListService {
    List createList(Member member, ListCreateRequestDto listCreateRequestDto);
    List updateList(Member member, Long listId, ListUpdateRequestDto listUpdateRequestDto);
    List getList(Long listId);
    List changeListIsArchived(Member member, Long listId);
    java.util.List<List> getArchivedList(Member member, Long boardId);
    void checkBoardMember(List list, Member member, Action action);

}
