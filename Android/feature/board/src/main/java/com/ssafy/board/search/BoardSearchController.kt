package com.ssafy.board.search

import com.ssafy.board.GetBoardAndWorkspaceMemberUseCase
import com.ssafy.board.GetLabelUseCase
import com.ssafy.board.search.dto.SearchAllParameters
import com.ssafy.board.search.dto.toLabel
import com.ssafy.board.search.dto.toParamsInfo
import com.ssafy.model.search.DueDate
import com.ssafy.model.search.SearchParameters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class BoardSearchController(
    private val coroutineScope: CoroutineScope,
    private val getLabelUseCase: GetLabelUseCase,
    private val getBoardAndWorkspaceMemberUseCase: GetBoardAndWorkspaceMemberUseCase,
) {
    private val _searchAllParameters = MutableStateFlow(SearchAllParameters())
    val searchAllParameters = _searchAllParameters.asStateFlow()
    val searchParameters = _searchAllParameters.map {
        val dueDates = searchAllParameters.value.dueDateMap.filter { it.value.isSelected }.keys
        val noMember = searchAllParameters.value.noMember.second.isSelected
        val members = searchAllParameters.value.memberMap.filter { it.value.isSelected }.keys
        val noLabel = searchAllParameters.value.noLabel.second.isSelected
        val labels = searchAllParameters.value.labelMap.filter { it.value.isSelected }.keys
        val searchText = searchAllParameters.value.searchedText

        SearchParameters(
            searchText = searchText,
            noMember = noMember,
            members = members.map { it.memberId }.toSet(),
            dueDates = dueDates.toSet(),
            noLabel = noLabel,
            labels = labels.map { it.id }.toSet()
        )
    }

    fun updateDueDate(dueDate: DueDate) = coroutineScope.launch {
        val dueDateMap = searchAllParameters.value.dueDateMap.toMutableMap()

        when (dueDate) {
            DueDate.NO_DUE_DATE, DueDate.OVERDUE -> {
                dueDateMap[dueDate]?.let {
                    dueDateMap[dueDate] = it.copy(isSelected = !it.isSelected)
                }
            }

            DueDate.DUE_IN_THE_NEXT_DAY -> {
                dueDateMap[dueDate]?.let { paramInfo ->
                    dueDateMap[dueDate] = paramInfo.copy(isSelected = !paramInfo.isSelected)

                    if (paramInfo.isSelected) return@let
                    dueDateMap[DueDate.DUE_IN_THE_NEXT_WEEK]?.let {
                        dueDateMap[DueDate.DUE_IN_THE_NEXT_WEEK] = it.copy(isSelected = false)
                    }
                    dueDateMap[DueDate.DUE_IN_THE_NEXT_MONTH]?.let {
                        dueDateMap[DueDate.DUE_IN_THE_NEXT_MONTH] = it.copy(isSelected = false)
                    }
                }
            }

            DueDate.DUE_IN_THE_NEXT_WEEK -> {
                dueDateMap[dueDate]?.let { paramInfo ->
                    dueDateMap[dueDate] = paramInfo.copy(isSelected = !paramInfo.isSelected)

                    if (paramInfo.isSelected) return@let
                    dueDateMap[DueDate.DUE_IN_THE_NEXT_DAY]?.let {
                        dueDateMap[DueDate.DUE_IN_THE_NEXT_DAY] = it.copy(isSelected = false)
                    }
                    dueDateMap[DueDate.DUE_IN_THE_NEXT_MONTH]?.let {
                        dueDateMap[DueDate.DUE_IN_THE_NEXT_MONTH] = it.copy(isSelected = false)
                    }
                }
            }

            DueDate.DUE_IN_THE_NEXT_MONTH -> {
                dueDateMap[dueDate]?.let { paramInfo ->
                    dueDateMap[dueDate] = paramInfo.copy(isSelected = !paramInfo.isSelected)

                    if (paramInfo.isSelected) return@let
                    dueDateMap[DueDate.DUE_IN_THE_NEXT_DAY]?.let {
                        dueDateMap[DueDate.DUE_IN_THE_NEXT_DAY] = it.copy(isSelected = false)
                    }
                    dueDateMap[DueDate.DUE_IN_THE_NEXT_WEEK]?.let {
                        dueDateMap[DueDate.DUE_IN_THE_NEXT_WEEK] = it.copy(isSelected = false)
                    }
                }
            }
        }

        _searchAllParameters.emit(searchAllParameters.value.copy(dueDateMap = dueDateMap))
    }

    fun updateNoMember() = coroutineScope.launch(Dispatchers.IO) {
        val noMember = searchAllParameters.value.noMember
        _searchAllParameters.emit(
            searchAllParameters.value.copy(
                noMember = Pair(
                    noMember.first,
                    noMember.second.copy(isSelected = !noMember.second.isSelected)
                )
            )
        )
    }

    fun updateMember(memberId: Long) = coroutineScope.launch(Dispatchers.IO) {
        val memberMap = searchAllParameters.value.memberMap.toMutableMap()
        val member = memberMap.keys.firstOrNull { it.memberId == memberId } ?: return@launch
        memberMap[member]?.let {
            memberMap[member] = it.copy(isSelected = !it.isSelected)
        }
        _searchAllParameters.emit(searchAllParameters.value.copy(memberMap = memberMap))
    }

    fun updateNoLabel() = coroutineScope.launch(Dispatchers.IO) {
        val noLabel = searchAllParameters.value.noLabel
        _searchAllParameters.emit(
            searchAllParameters.value.copy(
                noLabel = Pair(
                    noLabel.first,
                    noLabel.second.copy(isSelected = !noLabel.second.isSelected)
                )
            )
        )
    }

    fun updateLabel(labelId: Long) = coroutineScope.launch(Dispatchers.IO) {
        val labelMap = searchAllParameters.value.labelMap.toMutableMap()
        val label = labelMap.keys.firstOrNull { it.id == labelId } ?: return@launch
        labelMap[label]?.let {
            labelMap[label] = it.copy(isSelected = !it.isSelected)
        }
        _searchAllParameters.emit(searchAllParameters.value.copy(labelMap = labelMap))
    }

    fun updateSearchText(searchText: String) = coroutineScope.launch(Dispatchers.IO) {
        _searchAllParameters.emit(searchAllParameters.value.copy(searchedText = searchText))
    }

    fun setSearchParams(workspaceId: Long, boardId: Long) =
        coroutineScope.launch(Dispatchers.IO) {
            combine(
                getBoardAndWorkspaceMemberUseCase(workspaceId, boardId),
                getLabelUseCase(boardId)
            ) { members, labels ->
                Pair(members, labels)
            }.collect {
                val (members, labels) = it
                _searchAllParameters.emit(
                    SearchAllParameters(
                        memberMap = members.associateWith { it.toParamsInfo() },
                        labelMap = labels.associate { it.toLabel() to it.toParamsInfo() },
                    )
                )
            }
        }
}
