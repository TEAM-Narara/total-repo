package com.ssafy.board.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.board.search.dto.SearchAllParameters
import com.ssafy.model.search.DueDate
import com.ssafy.model.search.Label
import com.ssafy.model.search.SearchParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BoardSearchViewModel @Inject constructor() : ViewModel() {

    private val _searchAllParameters = MutableStateFlow(SearchAllParameters())
    val searchAllParameters = _searchAllParameters.asStateFlow()

    fun updateDueDate(dueDate: DueDate) = viewModelScope.launch {
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

    fun updateMember(member: String) = viewModelScope.launch(Dispatchers.IO) {
        val memberMap = searchAllParameters.value.memberMap.toMutableMap()
        memberMap[member]?.let {
            memberMap[member] = it.copy(isSelected = !it.isSelected)
        }
        _searchAllParameters.emit(searchAllParameters.value.copy(memberMap = memberMap))
    }

    fun updateLabel(label: Label) = viewModelScope.launch(Dispatchers.IO) {
        val labelMap = searchAllParameters.value.labelMap.toMutableMap()
        labelMap[label]?.let {
            labelMap[label] = it.copy(isSelected = !it.isSelected)
        }
        _searchAllParameters.emit(searchAllParameters.value.copy(labelMap = labelMap))
    }

    fun updateSearchText(searchText: String) {
        searchAllParameters.value.searchedText = searchText
    }

    fun updateSearchParams(params: SearchParameters) = viewModelScope.launch(Dispatchers.IO) {
        with(params) {
            dueDates.forEach { updateDueDate(it) }
            members.forEach { updateMember(it) }
            labels.forEach { updateLabel(it) }
            searchAllParameters.value.searchedText = searchText
        }
    }

    fun getSearchParameters(): SearchParameters {
        val dueDates = searchAllParameters.value.dueDateMap.filter { it.value.isSelected }.keys
        val members = searchAllParameters.value.memberMap.filter { it.value.isSelected }.keys
        val labels = searchAllParameters.value.labelMap.filter { it.value.isSelected }.keys
        val searchText = searchAllParameters.value.searchedText

        return SearchParameters(
            searchText = searchText,
            members = members.toSet(),
            dueDates = dueDates.toSet(),
            labels = labels.toSet()
        )
    }
}
