package com.ssafy.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ssafy.database.dto.MemberBackground

@Dao
interface MemberBackgroundDao {

    @Query("""
            SELECT * 
            FROM member_background
            WHERE id == :id And isStatus != 'DELETE'
        """)
    suspend fun getMemberBackground(id: Long): MemberBackground

    @Query("""
            SELECT * 
            FROM member_background
            WHERE isStatus != 'DELETE'
        """)
    suspend fun getAllMemberBackgrounds(): List<MemberBackground>

    // 로컬에서 생성
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMemberBackground(memberBackground: MemberBackground): Long

    // 서버 변경사항 동기화
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemberBackgrounds(memberBackgrounds: List<MemberBackground>): List<Long>

    // 서버에 존재하지 않는 로컬 데이터 삭제
    @Query("DELETE FROM member_background WHERE id NOT IN (:ids)")
    suspend fun deleteMemberBackgroundsNotIn(ids: List<Long>)

    // 원격 삭제 (isStatus: 'STAY' -> isStatus: 'DELETE')
    @Update
    suspend fun updateMemberBackground(memberBackground: MemberBackground)

    // 로컬 삭제(isStatus: CREATE -> 즉시 삭제)
    @Delete
    suspend fun deleteMemberBackground(memberBackground: MemberBackground)
}