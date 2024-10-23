package com.narara.superboard.board.infrastrucutre;

import com.narara.superboard.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findAllByWorkSpaceId(Long workSpaceId); // workSpaceId
}
