package com.ssafy.home.createboard

import androidx.lifecycle.viewModelScope
import com.ssafy.board.CreateBoardUseCase
import com.ssafy.model.background.Cover
import com.ssafy.model.board.BoardDTO
import com.ssafy.model.board.Visibility
import com.ssafy.model.with.CoverType
import com.ssafy.model.workspace.WorkSpaceDTO
import com.ssafy.ui.networkstate.NetworkState
import com.ssafy.ui.viewmodel.BaseViewModel
import com.ssafy.workspace.GetWorkspaceListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateBoardViewModel @Inject constructor(
    private val getWorkspaceListUseCase: GetWorkspaceListUseCase,
    private val createBoardUseCase: CreateBoardUseCase,
) : BaseViewModel() {

    var boardData = BoardDTO(
        id = 0,
        name = "",
        workspaceId = 0,
        cover = Cover(CoverType.NONE, ""),
        isClosed = false,
        visibility = Visibility.WORKSPACE
    )

    private val _workspace = MutableStateFlow<List<WorkSpaceDTO>>(emptyList())
    val workspace = _workspace.asStateFlow()

    fun createBoard(onSuccess: () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        val iscConnected = NetworkState.isConnected.value
        createBoardUseCase(boardData, iscConnected).withUiState().collect {
            withMain { onSuccess() }
        }
    }

    fun getWorkspaceList() = viewModelScope.launch(Dispatchers.IO) {
        withSocketState { isConnected ->
            getWorkspaceListUseCase(isConnected).safeCollect { _workspace.emit(it) }
        }
    }

    fun changeBoardName(name: String) {
        boardData = boardData.copy(name = name)
    }

    fun changeWorkspaceId(workSpaceDTO: WorkSpaceDTO) {
        boardData = boardData.copy(workspaceId = workSpaceDTO.workspaceId)
    }

    fun changeCover(cover: Cover) {
        boardData = boardData.copy(cover = cover)
    }

    fun changeVisibility(visibility: Visibility) {
        boardData = boardData.copy(visibility = visibility)
    }

}
