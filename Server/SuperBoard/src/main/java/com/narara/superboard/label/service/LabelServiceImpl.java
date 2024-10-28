package com.narara.superboard.label.service;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.board.infrastructure.BoardRepository;
import com.narara.superboard.common.application.validator.ColorValidator;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.label.entity.Label;
import com.narara.superboard.label.infrastructure.LabelRepository;
import com.narara.superboard.label.interfaces.dto.LabelCreateRequestDto;
import com.narara.superboard.label.interfaces.dto.LabelUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService {

    private final BoardRepository boardRepository;
    private final LabelRepository labelRepository;

    private final ColorValidator colorValidator;

    @Override
    public Label createLabel(Long boardId, LabelCreateRequestDto createLabelRequestDto) {
        colorValidator.validateLabelColor(createLabelRequestDto);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new NotFoundEntityException(boardId, "보드"));

        Label label = Label.createLabel(board, createLabelRequestDto);

        return labelRepository.save(label);
    }

    @Override
    public Label getLabel(Long labelId) {
        return labelRepository.findById(labelId)
                .orElseThrow(() -> new NotFoundEntityException(labelId, "라벨"));
    }


    @Override
    public Label updateLabel(Long labelId, LabelUpdateRequestDto updateLabelRequestDto) {
        colorValidator.validateLabelColor(updateLabelRequestDto);

        Label label = getLabel(labelId);

        return label.updateLabel(updateLabelRequestDto);
    }

    @Override
    public void deleteLabel(Long labelId) {
        Label label = getLabel(labelId);
        labelRepository.delete(label);
    }

}
