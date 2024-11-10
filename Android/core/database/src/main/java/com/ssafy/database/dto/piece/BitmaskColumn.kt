package com.ssafy.database.dto.piece

import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class BitPosition(val position: Int)

// 바뀐 컬럼 비트 켜기
fun <T : Any> bitmaskColumn(prevBit: Long, dto1: T, dto2: T): Long {
    var newBit = prevBit

    val properties = dto1::class.memberProperties

    properties.forEach { prop ->
        val bitPosition = prop.findAnnotation<BitPosition>()?.position
            ?: return@forEach

        @Suppress("UNCHECKED_CAST")
        val typedProp = prop as KProperty1<T, *>
        val value1 = typedProp.get(dto1)
        val value2 = typedProp.get(dto2)

        if (value1 != value2) {
            newBit = newBit or (1L shl bitPosition)
        }
    }

    return newBit
}

fun <T : Any> getBitmaskDto(columnUpdate: Long, dto: T): T {
    val kClass = dto::class
    if (!kClass.isData) {
        throw IllegalArgumentException("Only data classes are supported")
    }

    val copyFunction = kClass.members.firstOrNull { it.name == "copy" }
        ?: throw IllegalArgumentException("No copy function found")

    val args = mutableMapOf<kotlin.reflect.KParameter, Any?>()

    kClass.memberProperties.forEach { prop ->
        val bitPosition = prop.findAnnotation<BitPosition>()?.position ?: return@forEach

        val bitSet = (columnUpdate and (1L shl bitPosition)) != 0L

        if (!bitSet) {
            if (prop.returnType.isMarkedNullable) {
                val parameter = copyFunction.parameters.firstOrNull { it.name == prop.name }
                if (parameter != null) {
                    args[parameter] = null
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    val newDto = copyFunction.callBy(args) as T

    return newDto
}
