package com.narara.superboard.list.service;

public interface ListMoveService {
    void moveListToTop(Long listId);

    void moveListToBottom(Long listId);

    void moveListBetween(Long listId, Long previousListId, Long nextListId);
}
