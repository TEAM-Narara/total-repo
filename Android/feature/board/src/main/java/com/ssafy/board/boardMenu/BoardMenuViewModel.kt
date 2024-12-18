package com.ssafy.board.boardMenu

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ssafy.board.DeleteBoardUseCase
import com.ssafy.board.GetBoardActivityUseCase
import com.ssafy.board.GetBoardMembersUseCase
import com.ssafy.board.GetBoardUseCase
import com.ssafy.board.GetBoardWatchStatusUseCase
import com.ssafy.board.ToggleBoardWatchUseCase
import com.ssafy.board.UpdateBoardUseCase
import com.ssafy.board.boardMenu.data.BoardMenuData
import com.ssafy.model.activity.BoardActivity
import com.ssafy.model.board.BoardDTO
import com.ssafy.model.board.MemberResponseDTO
import com.ssafy.model.board.UpdateBoardRequestDto
import com.ssafy.model.board.Visibility
import com.ssafy.model.workspace.WorkSpaceDTO
import com.ssafy.ui.uistate.UiState
import com.ssafy.ui.viewmodel.BaseViewModel
import com.ssafy.workspace.GetWorkspaceUseCase
import com.ssafy.workspace.UpdateWorkspaceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class BoardMenuViewModel @Inject constructor(
    private val getWorkspaceUseCase: GetWorkspaceUseCase,
    private val updateWorkspaceUseCase: UpdateWorkspaceUseCase,
    private val getBoardUseCase: GetBoardUseCase,
    private val updateBoardUseCase: UpdateBoardUseCase,
    private val getBoardMembersUseCase: GetBoardMembersUseCase,
    private val getBoardWatchStatusUseCase: GetBoardWatchStatusUseCase,
    private val toggleBoardWatchUseCase: ToggleBoardWatchUseCase,
    private val deleteBoardUseCase: DeleteBoardUseCase,
    private val getBoardActivityUseCase: GetBoardActivityUseCase
) : BaseViewModel() {

    private val _workspaceId = MutableStateFlow<Long?>(null)
    private val _boardId = MutableStateFlow<Long?>(null)
    val boardState: StateFlow<BoardMenuData?> =
        combine(_workspaceId, _boardId) { workspaceId, boardId ->
            if (workspaceId != null && boardId != null) workspaceId to boardId
            else null
        }.filterNotNull().flatMapLatest { (workspaceId, boardId) ->
            combine(
                getWorkspaceUseCase(workspaceId),
                getBoardUseCase(boardId),
                getBoardMembersUseCase(boardId),
                getBoardWatchStatusUseCase(boardId)
            ) { workspace: WorkSpaceDTO?, board: BoardDTO?, members: List<MemberResponseDTO>, watchStatus: Boolean? ->
                if (workspace != null && board != null) {
                    BoardMenuData(workspace, board, members, watchStatus ?: false)
                } else {
                    null.also { _uiState.emit(UiState.Error(ERROR)) }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val boardActivity: Flow<PagingData<BoardActivity>> = _boardId.filterNotNull()
        .flatMapLatest { boardId -> getBoardActivityUseCase(boardId) }
        .cachedIn(viewModelScope)

    fun changeBoardName(name: String) = withIO {
        val boardState = boardState.value ?: return@withIO
        val boardId = boardState.boardDto.id
        val cover = boardState.boardDto.cover
        val visibility = boardState.boardDto.visibility
        val boardRequestDto = UpdateBoardRequestDto(
            name = name,
            cover = cover,
            visibility = visibility
        )
        withSocketState { isConnected ->
            updateBoardUseCase(boardId, boardRequestDto, isConnected).withUiState().collect()
        }
    }

    fun changeWorkspaceName(name: String) = withIO {
        val workspaceId = _workspaceId.value ?: return@withIO
        withSocketState { isConnected ->
            updateWorkspaceUseCase(workspaceId, name, isConnected).withUiState().collect()
        }
    }


    fun changeWatch(watchStatus: Boolean) = withIO {
        val boardState = boardState.value ?: return@withIO
        val boardId = boardState.boardDto.id
        val isWatch = boardState.watchStatus

        if (isWatch == watchStatus) return@withIO
        withSocketState { isConnected ->
            toggleBoardWatchUseCase(boardId, isConnected).withUiState().collect()
        }
    }

    fun changeVisibility(visibility: Visibility) = withIO {
        val boardState = boardState.value ?: return@withIO
        val boardId = boardState.boardDto.id
        val name = boardState.boardDto.name
        val cover = boardState.boardDto.cover
        val boardRequestDto = UpdateBoardRequestDto(
            name = name,
            cover = cover,
            visibility = visibility
        )
        withSocketState { isConnected ->
            updateBoardUseCase(boardId, boardRequestDto, isConnected).withUiState().collect()
        }
    }

    fun deleteBoard(onSuccess: () -> Unit) = withIO {
        val boardId = _boardId.value ?: return@withIO
        withSocketState { isConnected ->
            deleteBoardUseCase(boardId, isConnected).withUiState().collect {
                withMain { onSuccess() }
            }
        }
    }

    fun setBoardId(id: Long) = _boardId.update { id }
    fun setWorkspaceId(id: Long) = _workspaceId.update { id }

    companion object {
        const val ERROR = "보드 정보를 가져오는데 실패했습니다.\n잠시 뒤에 시도해주세요."
    }
}
