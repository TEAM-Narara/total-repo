package com.ssafy.card.card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.model.card.CardDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardViewModel @Inject constructor() : ViewModel() {
    private val _cardDetail = MutableStateFlow(CardDTO())
    val cardDetail = _cardDetail.asStateFlow()

    fun getCardDetail(cardId: Long) = viewModelScope.launch(Dispatchers.IO) {
        // TODO : 앱 db에서 cardId에 해당하는 카드 정보를 가져와서 _cardDetail에 저장
        _cardDetail.value = CardDTO()
    }

}