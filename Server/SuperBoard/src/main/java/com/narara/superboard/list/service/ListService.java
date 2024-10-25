package com.narara.superboard.list.service;

import com.narara.superboard.list.entity.List;
import com.narara.superboard.list.interfaces.dto.ListCreateRequestDto;
import com.narara.superboard.list.interfaces.dto.ListUpdateRequestDto;

public interface ListService {
    List createList(ListCreateRequestDto listCreateRequestDto);
    List updateList(Long listId, ListUpdateRequestDto listUpdateRequestDto);
    List getList(Long listId);
    List changeListIsArchived(Long listId);
}
