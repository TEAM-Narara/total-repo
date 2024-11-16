package com.ssafy.card.label

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import com.ssafy.board.CreateLabelUseCase
import com.ssafy.board.DeleteLabelUseCase
import com.ssafy.board.UpdateLabelUseCase
import com.ssafy.card.GetLabelListWithCardLabelUseCase
import com.ssafy.card.UpdateCardLabelUseCase
import com.ssafy.card.label.data.LabelData
import com.ssafy.card.label.data.toLabelData
import com.ssafy.model.label.CreateLabelRequestDto
import com.ssafy.model.label.UpdateLabelRequestDto
import com.ssafy.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
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
    private val updateLabelUseCase: UpdateLabelUseCase,
    private val deleteLabelUseCase: DeleteLabelUseCase,
    private val updateCardLabelUseCase: UpdateCardLabelUseCase,
) : BaseViewModel() {
    private var _boardId: MutableStateFlow<Long?> = MutableStateFlow(null)
    fun setBoardId(boardId: Long) = _boardId.update { boardId }
    private var _cardId: MutableStateFlow<Long?> = MutableStateFlow(null)
    fun setCardId(cardId: Long) = _cardId.update { cardId }

    val labelList: StateFlow<List<LabelData>?> = combine(
        _boardId,
        _cardId
    ) { boardId, cardId ->
        if (boardId == null || cardId == null) null
        else Pair(boardId, cardId)
    }.filterNotNull().flatMapLatest {
        getLabelListWithCardLabelUseCase(boardId = it.first, cardId = it.second).map { labels ->
            labels?.map { it.toLabelData() }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    fun createLabel(color: Color, description: String) = viewModelScope.launch(Dispatchers.IO) {
        val boardId = _boardId.value ?: return@launch
        withSocketState { isConnected ->
            createLabelUseCase(
                boardId = boardId,
                createLabelRequestDto = CreateLabelRequestDto(
                    color = color.toLong(),
                    name = description
                ),
                isConnected = isConnected
            )
        }
    }

    private fun Color.toLong() = (value shr 32).toLong()

    fun updateLabel(id: Long, color: Color, description: String) = viewModelScope.launch(Dispatchers.IO) {
        withSocketState { isConnected ->
            updateLabelUseCase(
                boardId = id,
                updateLabelRequestDto = UpdateLabelRequestDto(
                    color = color.toLong(),
                    name = description,
                ),
                isConnected = isConnected,
            )
        }
    }

    fun deleteLabel(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        withSocketState { isConnected ->
            deleteLabelUseCase(id, isConnected)
        }
    }

    fun selectLabel(id: Long, isSelected: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        val cardId = _cardId.value ?: return@launch
        withSocketState { isConnected ->
            updateCardLabelUseCase(
                cardId = cardId,
                labelId = id,
                isActivated = isSelected,
                isConnected = isConnected
            )
        }
    }
}
