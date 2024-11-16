package com.ssafy.database.dto.bitmask

data class UpdateListBitmaskDTO(
    @BitPosition(0) val name: String?,
)

data class UpdateListOrderBitmaskDTO(
    @BitPosition(1) val myOrder: Long?,
)

data class UpdateListArchiveBitmaskDTO(
    @BitPosition(2) val isArchived: Boolean?
)
