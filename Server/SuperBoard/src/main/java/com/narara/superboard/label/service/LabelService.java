package com.narara.superboard.label.service;

import com.narara.superboard.label.entity.Label;

public interface LabelService {

    Label createLabel(Long boardId, CreateLabelRequestDto createLabelRequestDto);

}
