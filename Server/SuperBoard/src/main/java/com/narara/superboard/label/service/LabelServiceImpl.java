package com.narara.superboard.label.service;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.board.infrastructure.BoardRepository;
import com.narara.superboard.board.service.kafka.BoardOffsetService;
import com.narara.superboard.cardlabel.entity.CardLabel;
import com.narara.superboard.cardlabel.infrastructrue.CardLabelRepository;
import com.narara.superboard.common.application.validator.ColorValidator;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.label.entity.Label;
import com.narara.superboard.label.infrastructure.LabelRepository;
import com.narara.superboard.label.interfaces.dto.LabelCreateRequestDto;
import com.narara.superboard.label.interfaces.dto.LabelUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService {

    private final BoardRepository boardRepository;
    private final LabelRepository labelRepository;

    private final ColorValidator colorValidator;
    private final BoardOffsetService boardOffsetService;
    private final CardLabelRepository cardLabelRepository;

    @Transactional
    @Override
    public Label createLabel(Long boardId, LabelCreateRequestDto createLabelRequestDto) {
        colorValidator.validateLabelColor(createLabelRequestDto);

        Board board = boardRepository.findByIdAndIsDeletedFalse(boardId)
                .orElseThrow(() -> new NotFoundEntityException(boardId, "보드"));

        Label label = Label.createLabel(board, createLabelRequestDto);
        Label savedLabel = labelRepository.save(label);
        boardOffsetService.saveAddLabel(savedLabel); // Websocket 라벨 추가

        return savedLabel;
    }

    @Override
    public Label getLabel(Long labelId) {
        return labelRepository.findById(labelId)
                .orElseThrow(() -> new NotFoundEntityException(labelId, "라벨"));
    }

    @Transactional
    @Override
    public Label updateLabel(Long labelId, LabelUpdateRequestDto updateLabelRequestDto) {
        colorValidator.validateLabelColor(updateLabelRequestDto);

        Label label = getLabel(labelId);
        Label savedLabel = label.updateLabel(updateLabelRequestDto);
        boardOffsetService.saveEditLabel(savedLabel); //Websocket 라벨 업데이트

        return savedLabel;
    }

    @Transactional
    @Override
    public void deleteLabel(Long labelId) {
        Label label = getLabel(labelId);

        //관련 카드라벨 다 삭제
        List<CardLabel> cardLabelList = cardLabelRepository.findByLabel(label);
        cardLabelRepository.deleteAll(cardLabelList);

        labelRepository.delete(label);

        boardOffsetService.saveDeleteLabel(label); //Websocket 라벨 삭제
    }

    @Override
    public List<Label> getAllLabelsByBoardId(Long boardId) {
        Board board = boardRepository.findByIdAndIsDeletedFalse(boardId)
                .orElseThrow(() -> new NotFoundEntityException(boardId, "보드"));

        return labelRepository.findAllByBoard(board);
    }
}
