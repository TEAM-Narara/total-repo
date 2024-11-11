package com.ssafy.database.dto.bitmask

import com.ssafy.model.with.CoverType
import com.ssafy.model.with.DataStatus
import kotlinx.serialization.Serializable

data class UpdateCardBitmaskDTO(
    @BitPosition(1) val name: String?,
    @BitPosition(2) val description: String?,
    @BitPosition(3) val startAt: Long?,
    @BitPosition(4) val endAt: Long?,
    val cover: CardCoverBitmask?
)

@Serializable
data class CardCoverBitmask(
    @BitPosition(5) val type: CoverType?,
    @BitPosition(6) val value: String?,

    @Transient
    val id: Long = 0,
    @Transient
    val isStatus: DataStatus = DataStatus.STAY
)

data class UpdateCardListIdBitmaskDTO(
    @BitPosition(0) val listId: Long?
)

data class UpdateCardOrderBitmaskDTO(
    @BitPosition(7) val myOrder: Long?
)

data class UpdateCardArchiveBitmaskDTO(
    @BitPosition(8) val isArchived: Boolean?
)