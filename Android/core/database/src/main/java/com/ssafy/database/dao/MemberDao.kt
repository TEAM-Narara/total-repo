package com.ssafy.database.dao

import androidx.room.Dao
import androidx.room.Delete
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
        WHERE id = :memberId
    """)
    fun getMember(memberId: Long): MemberEntity?

    // 멤버 단일 조회
    @Query("""
        SELECT * 
        FROM member 
        WHERE id = :memberId
    """)
    fun getMemberFlow(memberId: Long): Flow<MemberEntity?>

    // 로컬에서 생성
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: MemberEntity): Long

    // 서버 변경사항 동기화
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMembers(members: List<MemberEntity>): List<Long>

    @Update
    suspend fun updateMember(member: MemberEntity)

    // 로컬 삭제(isStatus: CREATE -> 즉시 삭제)
    @Delete
    suspend fun deleteMember(member: MemberEntity)

    // 서버에 존재하지 않는 로컬 데이터 삭제
    @Query("DELETE FROM member WHERE id NOT IN (:ids)")
    suspend fun deleteMembersNotIn(ids: List<Long>)
}