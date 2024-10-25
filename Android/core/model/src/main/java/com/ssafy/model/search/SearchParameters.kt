package com.ssafy.model.search

import kotlinx.serialization.Serializable

@Serializable
data class SearchParameters(
    // TODO : 멤버나 라벨에 대한 타입이 변경 될 수 있음.
    val searchText: String = "",
    val members: Set<String> = setOf("손오공-1", "손오공-3"),
    val dueDates: Set<DueDate> = setOf(DueDate.NO_DUE_DATE, DueDate.DUE_IN_THE_NEXT_DAY),
    val labels: Set<Label> = setOf(
        Label(
            content = "라벨2",
            color = 0xFFFF0000L
        ),

        Label("label2", 0xFFFF00FF),
        Label("label3", 0xFFFF00FF),
        Label("label4", 0xFFFF00FF),
        Label("label5", 0xFFFF00FF)
    ),
)

@Serializable
data class Label(
    val content: String,
    val color: Long
)
