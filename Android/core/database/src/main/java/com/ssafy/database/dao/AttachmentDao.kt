package com.ssafy.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ssafy.database.dto.Alert
import com.ssafy.database.dto.Attachment

@Dao
interface AttachmentDao {

    @Query("SELECT * FROM attachment WHERE id == :id And isStatus != 'DELETE'")
    suspend fun getAttachment(id: Long): Attachment

    @Query("SELECT * FROM attachment WHERE isStatus != 'DELETE'")
    suspend fun getAllAttachments(): List<Attachment>

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