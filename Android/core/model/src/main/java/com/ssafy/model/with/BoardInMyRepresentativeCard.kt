package com.ssafy.model.with

data class BoardInMyRepresentativeCard(
    val id: Long,
    val workspaceId: Long,
    val name: String,
    val coverType: String?,
    val coverValue: String?,
    val visibility: String,
    val isClosed: Boolean,
    val isStatus: DataStatus = DataStatus.STAY,

    val cards: List<CardThumbnail>,

//    val lastListOrder: Long = 0L,
//    val offset: Long = 0L,
//    val version: Long = 0L
)