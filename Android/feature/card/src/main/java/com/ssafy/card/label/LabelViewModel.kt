package com.ssafy.card.label

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.viewModelScope
import com.ssafy.card.label.data.LabelData
import com.ssafy.designsystem.values.backgroundColorList
import com.ssafy.socket.GetSocketStateUseCase
import com.ssafy.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class LabelViewModel @Inject constructor(
    getSocketStateUseCase: GetSocketStateUseCase,
) : BaseViewModel(getSocketStateUseCase) {
    private var _cardId: MutableStateFlow<Long?> = MutableStateFlow(null)
    fun setCardId(boardId: Long) = _cardId.update { boardId }

    val labelList: StateFlow<List<LabelData>?> = _cardId.filterNotNull().flatMapLatest { boardId ->
        flow {
            emit(
                backgroundColorList.map {
                    LabelData(
                        id = it.toArgb().toLong(),
                        color = it,
                        description = "",
                        isSelected = true
                    )
                }
            )
        }.withUiState()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    fun createLabel(color: Color, description: String) {}
    fun updateLabel(id: Long, color: Color, description: String) {}
    fun deleteLabel(id: Long) {}
    fun selectLabel(id: Long, isSelected: Boolean) {}
}