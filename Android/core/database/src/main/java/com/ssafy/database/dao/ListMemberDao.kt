package com.ssafy.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.ssafy.database.dto.ListMemberEntity
import com.ssafy.database.dto.ListMemberAlarmEntity
import com.ssafy.database.dto.WorkspaceMemberEntity
import com.ssafy.database.dto.with.ListMemberWithMemberInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface ListMemberDao {

    // 서버에 연산할 리스트 멤버 조회
    @Query("""
        SELECT * 
        FROM list_member
        WHERE isStatus != 'STAY'
    """)
    suspend fun getLocalOperationListMember(): List<ListMemberEntity>

    // 서버에 연산할 리스트 멤버 알람 조회
    @Query("""
        SELECT * 
        FROM list_member_alarm
        WHERE isStatus != 'STAY'
    """)
    suspend fun getLocalOperationListMemberAlarm(): List<ListMemberAlarmEntity>

    // 워크스페이스 단일 조회
    @Query("SELECT * FROM list_member WHERE id = :id")
    fun getListMember(id: Long): ListMemberEntity

    // 리스트 멤버들 조회
    @Transaction
    @Query("""
        SELECT 
            list_member.id AS list_member_id,
            list_member.memberId AS list_member_memberId,
            list_member.listId AS list_member_listId,
            list_member.isStatus AS list_member_isStatus,
            member.id AS member_id,
            member.email AS member_email,
            member.nickname AS member_nickname,
            member.profileImageUrl AS member_profileImageUrl
        FROM list_member 
        INNER JOIN member ON member.id = list_member.memberId
        WHERE list_member.listId = :listId AND list_member.isStatus != 'DELETE'
    """)
    fun getListMembers(listId: Long): Flow<List<ListMemberWithMemberInfo>>

    // 상태 업데이트
    @Update
    suspend fun updateListMember(listMember: ListMemberEntity)

    // 로컬 삭제(isStatus: CREATE -> 즉시 삭제)
    @Delete
    suspend fun deleteLocalListMember(listMember: ListMemberEntity)

    // 서버 변경사항 동기화
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListMembers(listMembers: List<ListMemberEntity>): List<Long>

    // 서버에 존재하지 않는 로컬 데이터 삭제
    @Query("DELETE FROM list_member WHERE id NOT IN (:ids)")
    suspend fun deleteListMembersNotIn(ids: List<Long>)
}