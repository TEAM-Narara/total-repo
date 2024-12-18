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
    val type: CoverType = CoverType.NONE,
    val value: String = "NONE",

    @Transient
    val id: Long = 0,
    @Transient
    val isStatus: DataStatus = DataStatus.STAY
) {
    companion object {
        const val KEY = "cover"
    }
}

fun CoverDto.toCover() = Cover(
    id = id,
    type = when {
        color != 0L -> CoverType.COLOR
        imgPath.isNotEmpty() -> CoverType.IMAGE
        else -> CoverType.NONE
    },
    value = when {
        color != 0L -> "#${color.toString(16).padStart(8, '0')}"
        imgPath.isNotEmpty() -> imgPath
        else -> ""
    },
    isStatus = isStatus
)

fun Cover.toCoverDto() = CoverDto(
    id = id,
    color = when (type) {
        CoverType.COLOR -> value.removePrefix("#").toLong(16)
        else -> 0L
    },
    imgPath = when (type) {
        CoverType.IMAGE -> value
        else -> ""
    },
    isStatus = isStatus
)
