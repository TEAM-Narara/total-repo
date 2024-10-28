package com.narara.superboard.label.service;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.board.infrastructure.BoardRepository;
import com.narara.superboard.common.application.validator.ColorValidator;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.label.entity.Label;
import com.narara.superboard.label.infrastructure.LabelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService {

    private final BoardRepository boardRepository;
    private final LabelRepository labelRepository;

    private final ColorValidator colorValidator;

    @Override
    public Label createLabel(Long boardId, CreateLabelRequestDto createLabelRequestDto) {
        colorValidator.validateLabelColor(createLabelRequestDto);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new NotFoundEntityException(boardId, "보드"));

        Label label = Label.createLabel(board, createLabelRequestDto);

        return labelRepository.save(label);
    }
}
