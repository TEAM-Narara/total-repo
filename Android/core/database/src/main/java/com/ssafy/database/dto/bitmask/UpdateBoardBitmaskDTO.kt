package com.ssafy.database.dto.bitmask

import com.ssafy.model.board.Visibility
import com.ssafy.model.with.CoverType
import com.ssafy.model.with.DataStatus
import kotlinx.serialization.Serializable

data class UpdateBoardBitmaskDTO(
    @BitPosition(0) val name: String?,
    val cover: CoverBitmask?,
    @BitPosition(3) val visibility: Visibility?,
)

data class UpdateBoardArchiveBitmaskDTO(
    @BitPosition(4) val isClosed: Boolean?
)

@Serializable
data class CoverBitmask(
    @BitPosition(1) val type: CoverType?,
    @BitPosition(2) val value: String?,

    @Transient
    val id: Long = 0,
    @Transient
    val isStatus: DataStatus = DataStatus.STAY
)