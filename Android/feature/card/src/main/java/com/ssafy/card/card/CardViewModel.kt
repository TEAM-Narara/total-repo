package com.ssafy.card.card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.model.card.CardDTO
import com.ssafy.model.card.CommentDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class CardViewModel @Inject constructor() : ViewModel() {
    private val _cardDTO = MutableStateFlow(CardDTO())
    val cardDTO = _cardDTO.asStateFlow()

    private val _userId = MutableStateFlow(1L)
    val userId = _userId.asStateFlow()

    fun getCardDetail(cardId: Long) = viewModelScope.launch(Dispatchers.IO) {
        // TODO : 앱 db에서 cardId에 해당하는 카드 정보를 가져와서 _cardDetail에 저장
        _cardDTO.value = CardDTO()
    }

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

    fun setCardContent(content: String) {
        cardDTO.value.editableContent = content
    }

    fun saveCardContent() = viewModelScope.launch(Dispatchers.IO) {
        val newCardDTO = _cardDTO.value.copy(content = _cardDTO.value.editableContent)
        _cardDTO.emit(newCardDTO)
        // TODO : 서버에 content 정보 전송
    }

    fun resetCardContent() {
        cardDTO.value.editableContent = cardDTO.value.content
    }

    fun setCommitContent(comment: CommentDTO, content: String) {
        comment.editableContent = content
    }

    fun saveCommitContent(comment: CommentDTO) = viewModelScope.launch(Dispatchers.IO) {
        val newComment = comment.copy(content = comment.editableContent)
        val newComments = (_cardDTO.value.comments - comment + newComment).sortedBy { it.commentId }
        val newCardDTO = _cardDTO.value.copy(comments = newComments)
        _cardDTO.emit(newCardDTO)
        // TODO : 서버에 댓글 정보 전송
    }

    fun resetCommitContent(comment: CommentDTO) {
        comment.editableContent = comment.content
    }

    fun setCardWatching(isWatching: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        val newCardDTO = _cardDTO.value.copy(isWatching = isWatching)
        _cardDTO.emit(newCardDTO)
        runCatching { /* TODO 서버에 isWatching 정보 전송 */ }
            .onSuccess { /* 성공 처리 */ }
            .onFailure { /* 실패 처리 */ }
    }

    fun setUserId() = viewModelScope.launch(Dispatchers.IO) {
        runCatching { /* TODO 사용자 정보를 가져와서 _userId에 저장 */ }
            .onSuccess { /* 성공 처리 */ }
            .onFailure { /* 실패 처리 */ }
    }

    fun addAttachment(filePath: String) {
        // TODO : 이미지를 S3나 앱 폴더에 저장하고, _cardDTO에 이미지 경로를 추가
    }

    fun deleteComment(comment: CommentDTO) = viewModelScope.launch(Dispatchers.IO) {
        val newCardDTO = _cardDTO.value.copy(comments = _cardDTO.value.comments - comment)
        _cardDTO.emit(newCardDTO)
        runCatching { /* 댓글 삭제 */ }
            .onSuccess { /* 성공 처리 */ }
            .onFailure { /* 실패 처리 */ }
    }

    fun addComment(message: String) = viewModelScope.launch(Dispatchers.IO) {
        val comment = CommentDTO(
            commentId = Random.nextLong(),
            userId = userId.value,
            nickname = "nickname",
            profileImageUrl = "profileImageUrl",
            content = message,
            date = Date().time
        )
        val newCardDTO = _cardDTO.value.copy(comments = _cardDTO.value.comments + comment)
        _cardDTO.emit(newCardDTO)
        runCatching { /* 댓글 추가 */ }
            .onSuccess { /* 성공 처리 */ }
            .onFailure { /* 실패 처리 */ }
    }

    fun toggleIsManager(id: Long, isManager: Boolean) {}
}
