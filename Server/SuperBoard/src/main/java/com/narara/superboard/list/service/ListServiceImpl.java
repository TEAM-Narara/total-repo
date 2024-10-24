package com.narara.superboard.list.service;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.board.infrastrucuture.BoardRepository;
import com.narara.superboard.common.application.validator.LastOrderValidator;
import com.narara.superboard.common.application.validator.NameValidator;
import com.narara.superboard.list.entity.List;
import com.narara.superboard.list.infrastrucure.ListRepository;
import com.narara.superboard.list.interfaces.dto.ListCreateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListServiceImpl implements ListService{

    private final NameValidator nameValidator;
    private final LastOrderValidator lastOrderValidator;

    private final BoardRepository boardRepository;
    private final ListRepository listRepository;

    @Override
    public List createList(ListCreateRequestDto listCreateRequestDto) {
        nameValidator.validateListNameIsEmpty(listCreateRequestDto);

        Board board = boardRepository.getReferenceById(listCreateRequestDto.boardId());
        Long lastListOrder = board.getLastListOrder();
        lastOrderValidator.checkValidListLastOrder(lastListOrder);

        List list = List.createList(listCreateRequestDto, lastListOrder);
        return listRepository.save(list);
    }
}
