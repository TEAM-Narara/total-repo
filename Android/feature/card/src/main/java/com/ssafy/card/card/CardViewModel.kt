package com.ssafy.card.card

import androidx.lifecycle.viewModelScope
import com.ssafy.card.DeleteCardUseCase
import com.ssafy.card.GetCardsUseCase
import com.ssafy.card.UpdateCardUseCase
import com.ssafy.card.period.data.PeriodData
import com.ssafy.comment.CreateCommentUseCase
import com.ssafy.comment.DeleteCommentUseCase
import com.ssafy.comment.UpdateCommentUseCase
import com.ssafy.member.GetMemberUseCase
import com.ssafy.model.card.CardDTO
import com.ssafy.model.card.CardUpdateRequestDto
import com.ssafy.model.card.CommentDTO
import com.ssafy.model.comment.CommentRequestDto
import com.ssafy.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CardViewModel @Inject constructor(
    private val getMemberUseCase: GetMemberUseCase,
    private val getCardUseCase: GetCardsUseCase,
    private val updateCardUseCase: UpdateCardUseCase,
    private val deleteCardUseCase: DeleteCardUseCase,
    private val createCommentUseCase: CreateCommentUseCase,
    private val updateCommentUseCase: UpdateCommentUseCase,
    private val deleteCommentUseCase: DeleteCommentUseCase,
) : BaseViewModel() {
    private var _cardId: MutableStateFlow<Long?> = MutableStateFlow(null)
    fun setCardId(cardId: Long) = _cardId.update { cardId }

    val cardDTO: StateFlow<CardDTO?> = _cardId.filterNotNull().flatMapLatest { cardId ->
        getCardUseCase(cardId)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    val userId = _cardId.filterNotNull().flatMapLatest {
        getMemberUseCase().map { it?.memberId }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    fun moveToArchive(popBack: () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { /* 카드 데이터를 아카이브로 이동 */ }
            .onSuccess { popBack() }
            .onFailure { /* 실패 처리 */ }
    }

    fun moveToDelete(popBack: () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { /* 카드 데이터를 삭제 */ }
            .onSuccess { popBack() }
            .onFailure { /* 실패 처리 */ }
    }

    fun setCardTitle(title: String) {
        cardDTO.value?.editableTitle = title
    }

    fun setCardContent(content: String) {
        cardDTO.value?.editableContent = content
    }

    fun saveCardTitle() = viewModelScope.launch(Dispatchers.IO) {
        val card = cardDTO.value ?: return@launch
        if (card.editableTitle.isEmpty()) {
            resetCardTitle()
            return@launch
        }
        withSocketState { isConnected ->
            updateCardUseCase(
                cardUpdateRequestDto = CardUpdateRequestDto(
                    cardId = card.cardId,
                    name = card.editableTitle,
                    description = card.content,
                    startAt = card.startDate,
                    endAt = card.endDate,
                    cover = card.cover
                ),
                isConnected = isConnected
            )
        }
    }

    fun saveCardContent() = viewModelScope.launch(Dispatchers.IO) {
        val card = cardDTO.value ?: return@launch
        withSocketState { isConnected ->
            updateCardUseCase(
                cardUpdateRequestDto = CardUpdateRequestDto(
                    cardId = card.cardId,
                    name = card.title,
                    description = card.editableContent,
                    startAt = card.startDate,
                    endAt = card.endDate,
                    cover = card.cover
                ),
                isConnected = isConnected
            )
        }
    }

    fun resetCardTitle() {
        cardDTO.value?.editableTitle = cardDTO.value?.title ?: return
    }

    fun resetCardContent() {
        cardDTO.value?.editableContent = cardDTO.value?.content
    }

    fun setCommitContent(comment: CommentDTO, content: String) {
        comment.editableContent = content
    }

    fun saveCommitContent(comment: CommentDTO) = viewModelScope.launch(Dispatchers.IO) {
        withSocketState { isConnected ->
            updateCommentUseCase(
                commentId = comment.commentId,
                content = comment.editableContent,
                isConnected = isConnected
            )
        }
    }

    fun resetCommitContent(comment: CommentDTO) {
        comment.editableContent = comment.content
    }

    fun setCardWatching(isWatching: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        // TODO : card watch 정보 수정 usecase 연결
    }

    fun addAttachment(filePath: String) {
        // TODO : 이미지를 S3나 앱 폴더에 저장하고, _cardDTO에 이미지 경로를 추가
    }

    fun deleteComment(comment: CommentDTO) = viewModelScope.launch(Dispatchers.IO) {
        withSocketState { isConnected ->
            deleteCommentUseCase(
                commentId = comment.commentId,
                isConnected = isConnected
            )
        }
    }

    fun addComment(message: String) = viewModelScope.launch(Dispatchers.IO) {
        val card = cardDTO.value ?: return@launch
        withSocketState { isConnected ->
            createCommentUseCase(
                commentRequestDto = CommentRequestDto(
                    cardId = card.cardId,
                    content = message
                ),
                isConnected = isConnected
            )
        }
    }

    fun toggleIsManager(id: Long, isManager: Boolean) {}

    fun updatePeriod(periodData: PeriodData) = viewModelScope.launch {
        val card = cardDTO.value ?: return@launch
        withSocketState { isConnected ->
            updateCardUseCase(
                cardUpdateRequestDto = CardUpdateRequestDto(
                    cardId = card.cardId,
                    name = card.title,
                    description = card.editableContent,
                    startAt = periodData.startDate,
                    endAt = periodData.endDate,
                    cover = card.cover
                ),
                isConnected = isConnected
            )
        }
    }
}
