package com.ssafy.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ssafy.database.dto.Reply

@Dao
interface ReplyDao {

    // 카드에서 볼 댓글
    @Query("""
        SELECT * 
        FROM reply 
        WHERE cardId == :cardId And isStatus != 'DELETE'
        ORDER BY id DESC
    """)
    suspend fun getAllReplies(cardId: Long): List<Reply>

    // 로컬에서 생성
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReply(reply: Reply): Long

    // 서버 변경사항 동기화
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReplies(replies: List<Reply>): List<Long>

    // 서버에 존재하지 않는 로컬 데이터 삭제
    @Query("DELETE FROM reply WHERE id NOT IN (:ids)")
    suspend fun deleteRepliesNotIn(ids: List<Long>)

    // 원격 삭제 (isStatus: 'STAY' -> isStatus: 'DELETE')
    @Update
    suspend fun updateReply(reply: Reply)

    // 로컬 삭제(isStatus: CREATE -> 즉시 삭제)
    @Delete
    suspend fun deleteReply(reply: Reply)
}