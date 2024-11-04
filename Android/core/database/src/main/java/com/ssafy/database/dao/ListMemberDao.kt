package com.ssafy.database.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ssafy.database.dto.SbListMember
import com.ssafy.database.dto.with.ListMemberWithMemberInfo

interface ListMemberDao {

    // 리스트 멤버들 조회
    @Query("""
        SELECT *
        FROM list_member
        WHERE listId == :listId
    """)
    suspend fun getListMembers(listId: Long): List<ListMemberWithMemberInfo>

    // 서버 변경사항 동기화
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListMembers(listMembers: List<SbListMember>): List<Long>

    // 서버에 존재하지 않는 로컬 데이터 삭제
    @Query("DELETE FROM list_member WHERE id NOT IN (:ids)")
    suspend fun deleteListMembersNotIn(ids: List<Long>)
}