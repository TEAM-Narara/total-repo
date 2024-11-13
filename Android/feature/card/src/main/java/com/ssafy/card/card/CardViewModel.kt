package com.ssafy.card.card

import androidx.lifecycle.viewModelScope
import com.ssafy.card.CreateAttachmentUseCase
import com.ssafy.card.DeleteAttachmentUseCase
import com.ssafy.card.DeleteCardUseCase
import com.ssafy.card.GetCardMemberUseCase
import com.ssafy.card.GetCardsUseCase
import com.ssafy.card.SetCardAlertStatusUseCase
import com.ssafy.card.SetCardArchiveUseCase
import com.ssafy.card.SetCardRepresentativeUseCase
import com.ssafy.card.UpdateAttachmentToCoverUseCase
import com.ssafy.card.UpdateCardUseCase
import com.ssafy.card.member.data.ManagerData
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
import kotlinx.coroutines.flow.collect
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
class CardViewModel @Inject constructor(
    private val getMemberUseCase: GetMemberUseCase,
    private val getCardUseCase: GetCardsUseCase,
    private val updateCardUseCase: UpdateCardUseCase,
    private val deleteCardUseCase: DeleteCardUseCase,
    private val setCardArchiveUseCase: SetCardArchiveUseCase,
    private val createCommentUseCase: CreateCommentUseCase,
    private val updateCommentUseCase: UpdateCommentUseCase,
    private val deleteCommentUseCase: DeleteCommentUseCase,
    private val createAttachmentUseCase: CreateAttachmentUseCase,
    private val deleteAttachmentUseCase: DeleteAttachmentUseCase,
    private val updateAttachmentToCoverUseCase: UpdateAttachmentToCoverUseCase,
    private val getCardMemberUseCase: GetCardMemberUseCase,
    private val setCardRepresentativeUseCase: SetCardRepresentativeUseCase,
    private val setCardAlertStatusUseCase: SetCardAlertStatusUseCase,
) : BaseViewModel() {
    private var _workspaceId: MutableStateFlow<Long?> = MutableStateFlow(null)
    fun setWorkspaceId(workspaceId: Long) = _workspaceId.update { workspaceId }
    private var _boardId: MutableStateFlow<Long?> = MutableStateFlow(null)
    fun setBardId(boardId: Long) = _boardId.update { boardId }
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

    val careMemberList = combine(_workspaceId, _boardId, _cardId) { workspaceId, boardId, cardId ->
        if (workspaceId == null || boardId == null || cardId == null) null
        else Triple(workspaceId, boardId, cardId)
    }.filterNotNull().flatMapLatest {
        val (workspaceId, boardId, cardId) = it
        getCardMemberUseCase(workspaceId, boardId, cardId).map {
            it.map {
                ManagerData(
                    id = it.memberId,
                    nickname = it.nickname,
                    email = it.email,
                    profileUrl = it.profileImageUrl,
                    isManager = it.isRepresentative
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    fun moveToArchive(popBack: () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        val cardId = _cardId.value ?: return@launch
        withSocketState { isConnected ->
            setCardArchiveUseCase(cardId, isConnected)
            launch(Dispatchers.Main) { popBack() }
        }
    }

    fun moveToDelete(popBack: () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        val cardId = _cardId.value ?: return@launch
        withSocketState { isConnected ->
            deleteCardUseCase(cardId, isConnected)
            launch(Dispatchers.Main) { popBack() }
        }
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
                cardId = card.cardId,
                cardUpdateRequestDto = CardUpdateRequestDto(
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
                cardId = card.cardId,
                cardUpdateRequestDto = CardUpdateRequestDto(
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
        val cardId = _cardId.value ?: return@launch
        withSocketState { isConnected: Boolean ->
            setCardAlertStatusUseCase(cardId, isWatching, isConnected).withUiState().collect()
        }
    }

    fun addAttachment(filePath: String) = viewModelScope.launch(Dispatchers.IO) {
        val cardId = _cardId.value ?: return@launch
        withSocketState { isConnected ->
            createAttachmentUseCase(cardId, filePath, isConnected)
        }
    }

    // TODO 이제 토글이 아니라 card의 cover 값을 변경합니다.
    fun updateAttachmentToCover(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        withSocketState { isConnected ->
            updateAttachmentToCoverUseCase(id, isConnected).withUiState().collect()
        }
    }

    fun deleteAttachment(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        withSocketState { isConnected ->
            deleteAttachmentUseCase(id, isConnected)
        }
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

    fun toggleIsManager(
        id: Long,
        isManager: Boolean,
        onError: (Throwable) -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        val cardId = _cardId.value ?: return@launch
        withSocketState { isConnected: Boolean ->
            setCardRepresentativeUseCase(cardId, id, isManager, isConnected).withUiState(onError)
                .collect()
        }
    }

    fun updatePeriod(periodData: PeriodData) = viewModelScope.launch(Dispatchers.IO) {
        val card = cardDTO.value ?: return@launch
        withSocketState { isConnected ->
            updateCardUseCase(
                cardId = card.cardId,
                cardUpdateRequestDto = CardUpdateRequestDto(
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
