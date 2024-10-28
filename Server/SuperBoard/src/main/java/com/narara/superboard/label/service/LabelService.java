package com.narara.superboard.label.service;

import com.narara.superboard.label.entity.Label;
import com.narara.superboard.label.interfaces.dto.LabelCreateRequestDto;
import com.narara.superboard.label.interfaces.dto.LabelUpdateRequestDto;

import java.util.List;

public interface LabelService {

    Label createLabel(Long boardId, LabelCreateRequestDto createLabelRequestDto);
    Label getLabel(Long boardId);
    Label updateLabel(Long labelId, LabelUpdateRequestDto updateLabelRequestDto);
    void deleteLabel(Long labelId);
    List<Label> getAllLabelsByBoardId(Long boardId);

}
