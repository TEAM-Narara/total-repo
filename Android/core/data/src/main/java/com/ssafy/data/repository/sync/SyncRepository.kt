package com.ssafy.data.repository.sync

interface SyncRepository {

    suspend fun syncMemberBackgroundList()

    suspend fun syncWorkspaceList()

    suspend fun syncBoardList()

    suspend fun syncListList()

    suspend fun syncCardList()

    suspend fun syncCardMemberList()

    suspend fun syncCardLabelList()

    suspend fun syncCardAttachmentList()

    suspend fun syncCommentList()

}
