package com.ssafy.card.label

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.viewModelScope
import com.ssafy.board.CreateLabelUseCase
import com.ssafy.card.GetLabelListWithCardLabelUseCase
import com.ssafy.card.label.data.LabelData
import com.ssafy.card.label.data.toLabelData
import com.ssafy.designsystem.values.backgroundColorList
import com.ssafy.model.label.LabelDTO
import com.ssafy.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class LabelViewModel @Inject constructor(
    getLabelListWithCardLabelUseCase: GetLabelListWithCardLabelUseCase,
    private val createLabelUseCase: CreateLabelUseCase,
) : BaseViewModel() {
    private var _cardId: MutableStateFlow<Long?> = MutableStateFlow(null)
    fun setCardId(cardId: Long) = _cardId.update { cardId }

    val labelList: StateFlow<List<LabelData>?> = _cardId.filterNotNull().flatMapLatest { cardId ->
        getLabelListWithCardLabelUseCase(boardId = 0, cardId = cardId).map { labels ->
            labels?.map { it.toLabelData() }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    fun createLabel(color: Color, description: String) = viewModelScope.launch {
//        createLabelUseCase()
    }
    fun updateLabel(id: Long, color: Color, description: String) {}
    fun deleteLabel(id: Long) {}
    fun selectLabel(id: Long, isSelected: Boolean) {}
}