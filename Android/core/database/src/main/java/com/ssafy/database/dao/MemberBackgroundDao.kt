package com.ssafy.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ssafy.database.dto.MemberBackgroundEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberBackgroundDao {

    // 멤버 배경 단일 조회
    @Query("""
            SELECT * 
            FROM member_background
            WHERE id = :id And isStatus != 'DELETE'
        """)
    fun getMemberBackground(id: Long): MemberBackgroundEntity?

    // 멤버 배경 모두 조회
    @Query("""
            SELECT * 
            FROM member_background
            WHERE isStatus != 'DELETE'
        """)
    fun getAllMemberBackgrounds(): Flow<List<MemberBackgroundEntity>>

    // 로컬에서 오프라인으로 생성한 멤버 배경 조회
    @Query("""
        SELECT * 
        FROM member_background
        WHERE isStatus = 'CREATE'
    """)
    suspend fun getLocalCreateMemberBackgrounds(): List<MemberBackgroundEntity>

    // 서버에 연산할 멤버 배경 조회
    @Query("""
        SELECT * 
        FROM member_background
        WHERE isStatus = 'UPDATE' OR isStatus = 'DELETE'
    """)
    suspend fun getLocalOperationMemberBackgrounds(): List<MemberBackgroundEntity>

    // 로컬에서 생성
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMemberBackground(memberBackground: MemberBackgroundEntity): Long

    // 서버 변경사항 동기화
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemberBackgrounds(memberBackgrounds: List<MemberBackgroundEntity>): List<Long>

    // 원격 삭제 (isStatus: 'STAY' -> isStatus: 'DELETE')
    @Query("UPDATE member_background SET isStatus = :status WHERE id = :id")
    suspend fun updateMemberBackground(id: Long, status: String)

    // 로컬 삭제(isStatus: CREATE -> 즉시 삭제)
    @Delete
    suspend fun deleteMemberBackground(memberBackground: MemberBackgroundEntity)

    // 서버에 존재하지 않는 로컬 데이터 삭제
    @Query("DELETE FROM member_background WHERE id NOT IN (:ids)")
    suspend fun deleteMemberBackgroundsNotIn(ids: List<Long>)
}