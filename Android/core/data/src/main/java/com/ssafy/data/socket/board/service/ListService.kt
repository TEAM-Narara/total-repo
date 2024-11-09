package com.ssafy.data.socket.board.service

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ssafy.data.socket.board.model.list.AddListRequestDto
import com.ssafy.data.socket.board.model.list.EditListArchiveRequestDto
import com.ssafy.data.socket.board.model.list.EditListRequestDto
import com.ssafy.database.dao.ListDao
import com.ssafy.database.dto.ListEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ListService @Inject constructor(
    private val listDao: ListDao,
    private val gson: Gson
) {
    suspend fun addList(data: JsonObject) {
        val dto = gson.fromJson(data, AddListRequestDto::class.java)
        listDao.insertList(
            ListEntity(
                id = dto.listId,
                boardId = dto.boardId,
                name = dto.name,
                myOrder = dto.myOrder,
                isArchived = dto.isArchived,
            )
        )
    }

    suspend fun editListArchive(data: JsonObject) {
        val dto = gson.fromJson(data, EditListArchiveRequestDto::class.java)
        val before = listDao.getList(dto.listId) ?: throw Exception("존재하지 않는 리스트입니다.")
        listDao.updateList(
            before.copy(
                isArchived = dto.isArchived
            )
        )
    }

    suspend fun editList(data: JsonObject) {
        val dto = gson.fromJson(data, EditListRequestDto::class.java)
        val before = listDao.getList(dto.listId) ?: throw Exception("존재하지 않는 리스트입니다.")
        listDao.updateList(
            before.copy(
                name = dto.name,
                myOrder = dto.myOrder,
                isArchived = dto.isArchived,
            )
        )
    }
}