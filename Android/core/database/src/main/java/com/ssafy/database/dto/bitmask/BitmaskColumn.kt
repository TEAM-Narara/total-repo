package com.ssafy.database.dto.bitmask

import com.ssafy.database.dto.BoardEntity
import com.ssafy.database.dto.CardEntity
import com.ssafy.database.dto.LabelEntity
import com.ssafy.database.dto.ListEntity
import com.ssafy.database.dto.piece.toArchiveBitDto
import com.ssafy.database.dto.piece.toBitArchiveDto
import com.ssafy.database.dto.piece.toBitDto
import com.ssafy.database.dto.piece.toBitOrderDto
import com.ssafy.database.dto.piece.toListIdBitDto
import com.ssafy.database.dto.piece.toOrderBitDto
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.full.memberProperties

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class BitPosition(val position: Int)

// TODO 특정 컬럼의 비트가 켜져있는지 확인

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

// 변경 정보를 가진 비트, 기존 정보를 가진 Entity를 매개변수로 받는다

// BOARD
fun getNullColumnBoard(bitmask: Long, boardEntity: BoardEntity): UpdateBoardBitmaskDTO {
    val board = boardEntity.toBitDto()

    val updateBoard = getBitmaskDto(bitmask, board)

    return updateBoard
}

fun getNullColumnBoardArchive(bitmask: Long, boardEntity: BoardEntity): UpdateBoardArchiveBitmaskDTO {
    val boardArchive = boardEntity.toBitArchiveDto()

    val updateBoardArchive = getBitmaskDto(bitmask, boardArchive)

    return updateBoardArchive
}

// LIST
fun getNullColumnList(bitmask: Long, listEntity: ListEntity): UpdateListBitmaskDTO {
    val list = listEntity.toBitDto()

    val updateList = getBitmaskDto(bitmask, list)

    return updateList
}

fun getNullColumnListOrder(bitmask: Long, listEntity: ListEntity): UpdateListOrderBitmaskDTO {
    val listOrder = listEntity.toBitOrderDto()

    val updateListOrder = getBitmaskDto(bitmask, listOrder)

    return updateListOrder
}

fun getNullColumnListArchive(bitmask: Long, listEntity: ListEntity): UpdateListArchiveBitmaskDTO {
    val listArchive = listEntity.toBitArchiveDto()

    val updateListArchive = getBitmaskDto(bitmask, listArchive)

    return updateListArchive
}

// CARD
fun getNullColumnCard(bitmask: Long, cardEntity: CardEntity): UpdateCardBitmaskDTO {
    val card = cardEntity.toBitDto()

    val updateCard = getBitmaskDto(bitmask, card)

    return updateCard
}

fun getNullColumnCardToListId(bitmask: Long, cardEntity: CardEntity): UpdateCardListIdBitmaskDTO {
    val cardToListId = cardEntity.toListIdBitDto()

    val updateCardToListId = getBitmaskDto(bitmask, cardToListId)

    return updateCardToListId
}

fun getNullColumnCardOrder(bitmask: Long, cardEntity: CardEntity): UpdateCardOrderBitmaskDTO {
    val cardOrder = cardEntity.toOrderBitDto()

    val updateCardOrder = getBitmaskDto(bitmask, cardOrder)

    return updateCardOrder
}

fun getNullColumnCardArchive(bitmask: Long, cardEntity: CardEntity): UpdateCardArchiveBitmaskDTO {
    val cardArchive = cardEntity.toArchiveBitDto()

    val updateCardArchive = getBitmaskDto(bitmask, cardArchive)

    return updateCardArchive
}

// LABEL
fun getNullColumnLabel(bitmask: Long, labelEntity: LabelEntity): UpdateLabelBitmaskDTO {
    val label = labelEntity.toBitDto()

    val updateLabel = getBitmaskDto(bitmask, label)

    return updateLabel
}
