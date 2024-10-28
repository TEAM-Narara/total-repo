package com.narara.superboard.label.infrastructure;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.label.entity.Label;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LabelRepository extends JpaRepository<Label, Long> {
    List<Label> findAllByBoard(Board board);
}
