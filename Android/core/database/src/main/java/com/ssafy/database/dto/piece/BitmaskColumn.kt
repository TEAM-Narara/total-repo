package com.ssafy.database.dto.piece

import kotlin.reflect.KParameter
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.instanceParameter
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
        if (bitPosition == null) {
            println("Property ${prop.name} does not have a BitPosition annotation.")
            return@forEach
        } else {
            println("Property ${prop.name} has a BitPosition at $bitPosition.")
        }

        val typedProp = prop as? KProperty1<T, *> ?: return@forEach
        val value1 = typedProp.get(dto1)
        val value2 = typedProp.get(dto2)

        if (value1 != value2) {
            println("Property ${prop.name} changed. Setting bit at position $bitPosition.")
            newBit = newBit or (1L shl bitPosition)
        }
    }

        return newBit
}

// 서버에 동기화할 DTO 생성
// 바뀌지 않은 컬럼은 null
fun <T : Any> getBitmaskDto(columnUpdate: Long, dto: T): T {
    val kClass = dto::class
    if (!kClass.isData) {
        throw IllegalArgumentException("Only data classes are supported")
    }

    val copyFunction = kClass.members.firstOrNull { it.name == "copy" }
        ?: throw IllegalArgumentException("No copy function found")

    val args = mutableMapOf<KParameter, Any?>()

    // 'copy' 함수의 인스턴스 파라미터를 설정
    val instanceParameter = copyFunction.instanceParameter
    if (instanceParameter != null) {
        args[instanceParameter] = dto
    }

    val copyParametersByName = copyFunction.parameters.associateBy { it.name }

    kClass.memberProperties.forEach { prop ->
        val bitPosition = prop.findAnnotation<BitPosition>()?.position ?: return@forEach

        val bitSet = (columnUpdate and (1L shl bitPosition)) != 0L

        val parameter = copyParametersByName[prop.name]
        if (parameter != null) {
            if (bitSet) {
                // 비트가 설정되어 있으면 원래 값을 유지
                args[parameter] = prop.getter.call(dto)
            } else {
                if (prop.returnType.isMarkedNullable) {
                    // 비트가 설정되지 않았고 속성이 nullable이면 null로 설정
                    args[parameter] = null
                } else {
                    // 비트가 설정되지 않았고 속성이 nullable이 아니면 기본값 사용 또는 예외 처리
                    // 기본값이 없으면 예외를 발생시켜야 합니다.
                    throw IllegalArgumentException("Non-nullable property '${prop.name}' cannot be set to null")
                }
            }
        }
    }

    // 필수 파라미터 중 아직 값이 설정되지 않은 경우 원래 값으로 채워줍니다.
    copyFunction.parameters.forEach { parameter ->
        if (!args.containsKey(parameter) && parameter.kind == KParameter.Kind.VALUE) {
            val prop = kClass.memberProperties.firstOrNull { it.name == parameter.name }
            if (prop != null) {
                args[parameter] = prop.getter.call(dto)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    val newDto = copyFunction.callBy(args) as T

    return newDto
}
