package com.ssafy.model.background

import com.ssafy.model.with.CoverType
import com.ssafy.model.with.DataStatus
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

data class CoverDto(
    val id: Long = 0,
    val color: Long,
    val imgPath: String,

    @Transient
    val isStatus: DataStatus = DataStatus.STAY
)


@Serializable
data class Cover(
    val type: CoverType,
    val value: String,

    @Transient
    val id: Long = 0,
    @Transient
    val isStatus: DataStatus = DataStatus.STAY
)

fun CoverDto.toCover() = Cover(
    id = id,
    type = when {
        color != 0L -> CoverType.COLOR
        imgPath.isNotEmpty() -> CoverType.IMAGE
        else -> CoverType.NONE
    },
    value = when {
        color != 0L -> color.toString()
        imgPath.isNotEmpty() -> imgPath
        else -> ""
    },
    isStatus = isStatus
)

fun Cover.CoverDto() = CoverDto(
    id = id,
    color = when (type) {
        CoverType.COLOR -> value.toLong()
        else -> 0L
    },
    imgPath = when (type) {
        CoverType.IMAGE -> value
        else -> ""
    },
    isStatus = isStatus
)
