package com.narara.superboard.label.interfaces;

import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.common.interfaces.response.ResponseMessage;
import com.narara.superboard.common.interfaces.response.StatusCode;
import com.narara.superboard.label.entity.Label;
import com.narara.superboard.label.interfaces.dto.LabelCreateRequestDto;
import com.narara.superboard.label.interfaces.dto.LabelSimpleResponseDto;
import com.narara.superboard.label.interfaces.dto.LabelUpdateRequestDto;
import com.narara.superboard.label.service.LabelService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "라벨")
@Controller
@RequiredArgsConstructor
public class LabelController implements LabelAPI {

    private final LabelService labelService;

    @Override
    public ResponseEntity<DefaultResponse<LabelSimpleResponseDto>> createLabel(
            @RequestParam Long boardId,
            @RequestBody LabelCreateRequestDto createLabelRequestDto) {

        Label label = labelService.createLabel(boardId, createLabelRequestDto);
        return new ResponseEntity<>(
                DefaultResponse.res(StatusCode.CREATED, ResponseMessage.LABEL_CREATE_SUCCESS,
                        LabelSimpleResponseDto.of(label)),
                HttpStatus.CREATED
        );
    }

    @Override
    public ResponseEntity<DefaultResponse<LabelSimpleResponseDto>> getLabel(@PathVariable Long boardId) {

        Label label = labelService.getLabel(boardId);
        return new ResponseEntity<>(
                DefaultResponse.res(StatusCode.OK, ResponseMessage.LABEL_FETCH_SUCCESS,
                        LabelSimpleResponseDto.of(label)),
                HttpStatus.OK
        );
    }

    @Override
    public ResponseEntity<DefaultResponse<LabelSimpleResponseDto>> updateLabel(
            @PathVariable Long labelId,
            @RequestBody LabelUpdateRequestDto updateLabelRequestDto) {

        Label label = labelService.updateLabel(labelId, updateLabelRequestDto);
        return new ResponseEntity<>(
                DefaultResponse.res(StatusCode.OK, ResponseMessage.LABEL_UPDATE_SUCCESS,
                        LabelSimpleResponseDto.of(label)),
                HttpStatus.OK
        );
    }

    @Override
    public ResponseEntity<DefaultResponse<Void>> deleteLabel(@PathVariable Long labelId) {

        labelService.deleteLabel(labelId);
        return new ResponseEntity<>(
                DefaultResponse.res(StatusCode.OK, ResponseMessage.LABEL_DELETE_SUCCESS),
                HttpStatus.OK
        );
    }

    @Override
    public ResponseEntity<DefaultResponse<List<LabelSimpleResponseDto>>> getAllLabelsByBoardId(
            @PathVariable Long boardId) {

        List<Label> labels = labelService.getAllLabelsByBoardId(boardId);
        List<LabelSimpleResponseDto> labelSimpleResponseDtoList = new ArrayList<>();
        for (Label label : labels) {
            labelSimpleResponseDtoList.add(LabelSimpleResponseDto.of(label));
        }
        return new ResponseEntity<>(
                DefaultResponse.res(StatusCode.OK, ResponseMessage.LABEL_ALL_FETCH_SUCCESS, labelSimpleResponseDtoList),
                HttpStatus.OK
        );
    }
}
