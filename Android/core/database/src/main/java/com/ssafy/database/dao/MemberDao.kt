package com.ssafy.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ssafy.database.dto.MemberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao {

    // 멤버 단일 조회
    @Query("""
        SELECT * 
        FROM member 
        WHERE id == :memberId
    """)
    fun getMembers(memberId: Long): Flow<MemberEntity>

    // 서버 변경사항 동기화
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMembers(members: List<MemberEntity>): List<Long>

    // 서버에 존재하지 않는 로컬 데이터 삭제
    @Query("DELETE FROM member WHERE id NOT IN (:ids)")
    suspend fun deleteMembersNotIn(ids: List<Long>)

    @Update
    suspend fun updateMember(member: MemberEntity)
}