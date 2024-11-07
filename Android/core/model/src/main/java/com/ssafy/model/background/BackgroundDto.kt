package com.ssafy.model.background

import com.ssafy.model.with.DataStatus
import kotlinx.serialization.Serializable

data class BackgroundDto(
    val id: Long = 0,
    val color: Long,
    val imgPath: String,

    @Transient
    val isStatus: DataStatus = DataStatus.STAY
)


@Serializable
data class Background(
    val type: BackgroundType,
    val value: String,

    val id: Long = 0,
    val isStatus: DataStatus = DataStatus.STAY
)

enum class BackgroundType {
    COLOR,
    IMAGE,
    NONE,
}

fun BackgroundDto.toBackground() = Background(
    id = id,
    type = when {
        color != 0L -> BackgroundType.COLOR
        imgPath.isNotEmpty() -> BackgroundType.IMAGE
        else -> BackgroundType.NONE
    },
    value = when {
        color != 0L -> color.toString()
        imgPath.isNotEmpty() -> imgPath
        else -> ""
    },
    isStatus = isStatus
)

fun Background.toBackgroundDto() = BackgroundDto(
    id = id,
    color = when (type) {
        BackgroundType.COLOR -> value.toLong()
        else -> 0L
    },
    imgPath = when (type) {
        BackgroundType.IMAGE -> value
        else -> ""
    },
    isStatus = isStatus
)
