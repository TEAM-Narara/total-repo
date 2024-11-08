package com.narara.superboard.label.interfaces;

import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.label.entity.Label;
import com.narara.superboard.label.interfaces.dto.LabelCreateRequestDto;
import com.narara.superboard.label.interfaces.dto.LabelSimpleResponseDto;
import com.narara.superboard.label.interfaces.dto.LabelUpdateRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/label")
public interface LabelAPI {

    @PostMapping
    @Operation(summary = "라벨 생성", description = "특정 보드에 라벨을 생성합니다.")
    ResponseEntity<DefaultResponse<LabelSimpleResponseDto>> createLabel(
            @RequestParam Long boardId,
            @RequestBody LabelCreateRequestDto createLabelRequestDto);

    @GetMapping("/{boardId}")
    @Operation(summary = "라벨 조회", description = "특정 보드의 라벨을 조회합니다.")
    ResponseEntity<DefaultResponse<LabelSimpleResponseDto>> getLabel(@PathVariable Long boardId);

    @PatchMapping("/{labelId}")
    @Operation(summary = "라벨 수정", description = "라벨 정보를 수정합니다.")
    ResponseEntity<DefaultResponse<LabelSimpleResponseDto>> updateLabel(
            @PathVariable Long labelId,
            @RequestBody LabelUpdateRequestDto updateLabelRequestDto);

    @DeleteMapping("/{labelId}")
    @Operation(summary = "라벨 삭제", description = "특정 라벨을 삭제합니다.")
    ResponseEntity<DefaultResponse<Void>> deleteLabel(@PathVariable Long labelId);

    @GetMapping("/all/{boardId}")
    @Operation(summary = "보드의 모든 라벨 조회", description = "특정 보드의 모든 라벨 목록을 조회합니다.")
    ResponseEntity<DefaultResponse<List<LabelSimpleResponseDto>>> getAllLabelsByBoardId(@PathVariable Long boardId);
}
