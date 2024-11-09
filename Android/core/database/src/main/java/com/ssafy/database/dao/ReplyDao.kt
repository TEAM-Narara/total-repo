package com.ssafy.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ssafy.database.dto.ReplyEntity
import com.ssafy.database.dto.piece.ReplyCount
import com.ssafy.database.dto.with.ReplyWithMemberInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface ReplyDao {

    // 댓글 단일 조회
    @Query("SELECT * FROM reply WHERE id = :replyId")
    fun getReply(replyId: Long): ReplyEntity?

    // 카드에서 볼 댓글
    @Query("""
        SELECT reply.*, 
               member.id AS member_id, 
               member.email AS member_email, 
               member.nickname AS member_nickname, 
               member.profileImageUrl AS member_profileImageUrl
        FROM reply 
        INNER JOIN member ON reply.memberId = member.id
        WHERE reply.cardId = :cardId AND reply.isStatus != 'DELETE'
        ORDER BY reply.createAt DESC
    """)
    fun getAllReplies(cardId: Long): Flow<List<ReplyWithMemberInfo>>

    // 댓글 수 조회
    @Query("""
        SELECT cardId, COUNT(*) AS count 
        FROM reply
        WHERE isStatus != 'DELETE' AND cardId IN (:cardIds)
        GROUP BY cardId
    """)
    fun getReplyCounts(cardIds: List<Long>): Flow<List<ReplyCount>>

    // 로컬에서 생성한 오프라인 댓글 조회
    @Query("""
        SELECT * 
        FROM reply
        WHERE isStatus = 'CREATE'
    """)
    suspend fun getLocalCreateReplies(): List<ReplyEntity>

    // 서버에 연산할 댓글 조회
    @Query("""
        SELECT * 
        FROM reply
        WHERE isStatus = 'UPDATE' OR isStatus = 'DELETE'
    """)
    suspend fun getLocalOperationReplies(): List<ReplyEntity>

    // 로컬에서 생성
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReply(reply: ReplyEntity): Long

    // 서버 변경사항 동기화
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReplies(replies: List<ReplyEntity>): List<Long>

    // 원격 삭제 (isStatus: 'STAY' -> isStatus: 'DELETE')
    @Update
    suspend fun updateReply(reply: ReplyEntity)

    // 로컬 삭제(isStatus: CREATE -> 즉시 삭제)
    @Delete
    suspend fun deleteReply(reply: ReplyEntity)

    // 서버에 존재하지 않는 로컬 데이터 삭제
    @Query("DELETE FROM reply WHERE id NOT IN (:ids)")
    suspend fun deleteRepliesNotIn(ids: List<Long>)
}