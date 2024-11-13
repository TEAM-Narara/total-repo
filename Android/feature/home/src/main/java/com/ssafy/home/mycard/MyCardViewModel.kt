package com.ssafy.home.mycard

import com.ssafy.card.GetMyCardUseCase
import com.ssafy.model.with.BoardInMyRepresentativeCard
import com.ssafy.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MyCardViewModel @Inject constructor(
    private val getMyCardUseCase: GetMyCardUseCase
) : BaseViewModel() {

    private val _myCardList = MutableStateFlow<List<BoardInMyRepresentativeCard>>(emptyList())
    val myCardList = _myCardList.asStateFlow()

    fun getMyCard() = withIO {
        getMyCardUseCase().safeCollect { _myCardList.emit(it) }
    }

}
