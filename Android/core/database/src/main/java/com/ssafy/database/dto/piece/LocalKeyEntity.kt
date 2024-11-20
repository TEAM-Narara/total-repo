package com.ssafy.database.dto.piece

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_key")
data class LocalKeyEntity(
    @PrimaryKey val id: LocalTable,
    val pk: Long = 0L,
)

enum class LocalTable(name: String) {
    WORKSPACE("WORKSPACE"),
    BOARD("BOARD"),
    LIST("LIST"),
    CARD("CARD"),
    REPLY("REPLY"),
    ATTACHMENT("ATTACHMENT")
}