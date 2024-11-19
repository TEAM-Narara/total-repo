package com.narara.superboard.board.infrastructure;

import com.narara.superboard.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardSearchRepository extends JpaRepository<Board, Long>, BoardSearchRepositoryCustom {
}
