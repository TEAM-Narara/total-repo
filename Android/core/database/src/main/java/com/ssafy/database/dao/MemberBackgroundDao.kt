package com.ssafy.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ssafy.database.dto.MemberBackground

@Dao
interface MemberBackgroundDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addAlbum(item: MemberBackground)

    @Query("SELECT * FROM member_background")
    suspend fun getAllMemberBackgrounds(): List<MemberBackground>
}