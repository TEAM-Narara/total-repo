package com.ssafy.home.createboard

import androidx.lifecycle.viewModelScope
import com.ssafy.model.board.BoardDTO
import com.ssafy.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateBoardViewModel @Inject constructor() : BaseViewModel() {

    fun createBoard(boardDTO: BoardDTO) = viewModelScope.launch(Dispatchers.IO) {
        TODO("Not yet implemented")
    }

}
