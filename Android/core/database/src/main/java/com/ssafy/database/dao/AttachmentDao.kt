package com.ssafy.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.ssafy.database.dto.Attachment

@Dao
interface AttachmentDao {
    @Query("SELECT * FROM attachment")
    suspend fun getAllAttachments(): List<Attachment>
}