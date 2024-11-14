package com.ssafy.model.search

import kotlinx.serialization.Serializable

@Serializable
data class SearchParameters(
    val searchText: String = "",
    val noMember: Boolean = false,
    val members: Set<Long> = setOf(),
    val dueDates: Set<DueDate> = setOf(),
    val noLabel: Boolean = false,
    val labels: Set<Long> = setOf(),
)

@Serializable
data class Label(
    val id: Long,
    val content: String,
    val color: Long
)