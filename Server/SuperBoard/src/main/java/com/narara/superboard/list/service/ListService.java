package com.narara.superboard.list.service;

import com.narara.superboard.list.entity.List;
import com.narara.superboard.list.interfaces.dto.ListCreateRequestDto;

public interface ListService {
    List createList(ListCreateRequestDto listCreateRequestDto);

}
