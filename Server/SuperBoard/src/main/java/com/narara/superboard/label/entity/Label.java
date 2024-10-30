package com.narara.superboard.label.entity;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.common.entity.BaseTimeEntity;
import com.narara.superboard.label.interfaces.dto.LabelCreateRequestDto;
import com.narara.superboard.label.interfaces.dto.LabelUpdateRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "label")
public class Label extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 기본키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;  // 보드 키

    @Column(name = "name", nullable = false)
    private String name;  // 이름

    @Column(name = "color", nullable = false)
    private Long color;  // 색상


    public static Label createLabel(Board board, LabelCreateRequestDto createLabelRequestDto) {
        return Label.builder()
                .board(board)
                .name(createLabelRequestDto.name())
                .color(createLabelRequestDto.color())
                .build();
    }

    public Label updateLabel(LabelUpdateRequestDto updateLabelRequestDto) {
        this.name = updateLabelRequestDto.name();
        this.color = updateLabelRequestDto.color();
        return this;
    }
}
