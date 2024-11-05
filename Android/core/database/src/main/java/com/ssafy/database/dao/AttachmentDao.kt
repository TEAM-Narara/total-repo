package com.ssafy.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.ssafy.database.dto.Attachment
import kotlinx.coroutines.flow.Flow

@Dao
interface AttachmentDao {

    // 로컬에서 생성한 오프라인 첨부파일 조회
    @Transaction
    @Query("""
        SELECT * 
        FROM attachment
        WHERE isStatus == 'CREATE'
    """)
    suspend fun getAllLocalAttachments(): List<Attachment>?

    // 서버에 연산할 첨부파일 조회
    @Transaction
    @Query("""
        SELECT * 
        FROM attachment
        WHERE isStatus == 'UPDATE' OR isStatus == 'DELETE'
    """)
    suspend fun getAllRemoteAttachments(): List<Attachment>?

    // 첨부파일 단일 조회
    @Query("SELECT * FROM attachment WHERE id == :id And isStatus != 'DELETE'")
    fun getAttachment(id: Long): Flow<Attachment>?

    // 카드의 커버이미지 조회
    @Query("SELECT * FROM attachment WHERE cardId == :cardId AND isCover == 1 AND isStatus != 'DELETE'")
    fun getCoverAttachment(cardId: Long): Flow<Attachment>

    // 카드의 첨부파일 조회
    @Query("SELECT * FROM attachment WHERE cardId == :cardId AND isStatus != 'DELETE'")
    fun getAllAttachments(cardId: Long): Flow<List<Attachment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttachment(attachment: Attachment): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttachments(attachments: List<Attachment>): List<Long>

    // 서버에 존재하지 않는 로컬 데이터 삭제
    @Query("DELETE FROM attachment WHERE id NOT IN (:ids)")
    suspend fun deleteAttachmentsNotIn(ids: List<Long>)

    // 원격 삭제(isStatus == STAY)
    @Update
    suspend fun updateAttachment(attachment: Attachment)

    // 로컬 삭제(isStatus == CREATE)
    @Delete
    suspend fun deleteAttachment(attachment: Attachment)
}