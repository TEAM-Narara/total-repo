package com.ssafy.database.dto.bitmask

data class UpdateLabelBitmaskDTO(
    @BitPosition(0) val name: String?,
    @BitPosition(1) val color: Long?
)
