package com.narara.superboard.board.service;

import com.narara.superboard.board.infrastructure.BoardSearchRepository;
import com.narara.superboard.workspace.interfaces.dto.MyBoardCollectionResponse.MyBoardWorkspaceCollectionDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {
    private final BoardSearchRepository boardSearchRepository;

    public List<MyBoardWorkspaceCollectionDto> searchBoardsAndWorkspaces(String keyword, Long memberId) {
        return boardSearchRepository.searchBoardsAndWorkspaces(keyword, memberId);
    }
}
