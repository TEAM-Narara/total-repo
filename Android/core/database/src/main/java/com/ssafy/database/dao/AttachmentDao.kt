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
    @Query("SELECT * FROM attachment WHERE isStatus != 'DELETE'")
    suspend fun getAllAttachments(): List<Attachment>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttachment(attachment: Attachment): Long

    // 원격 삭제(isStatus == STAY)
    @Update
    suspend fun updateAttachment(attachment: Attachment)

    // 로컬 삭제(isStatus == CREATE)
    @Delete
    suspend fun deleteAttachment(attachment: Attachment)
}