package com.ssafy.board.boardMenu

import com.ssafy.board.UpdateBoardUseCase
import com.ssafy.member.CreateMemberBackgroundUseCase
import com.ssafy.member.GetMemberBackgroundUseCase
import com.ssafy.model.background.Cover
import com.ssafy.model.with.CoverType
import com.ssafy.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class SelectBoardBackgroundViewModel @Inject constructor(
    private val getMemberBackgroundUseCase: GetMemberBackgroundUseCase,
    private val createMemberBackgroundUseCase: CreateMemberBackgroundUseCase,
    private val updateBoardUseCase: UpdateBoardUseCase
) : BaseViewModel() {

    private val _imagePathList = MutableStateFlow<List<String>>(emptyList())
    val imagePathList = _imagePathList.asStateFlow()

    fun loadMemberBackgrounds() = withIO {
        withSocketState { isConnected: Boolean ->
            getMemberBackgroundUseCase(isConnected).safeCollect { coverList ->
                val imagePathList = coverList.filter { it.type == CoverType.IMAGE }.map { it.value }
                _imagePathList.emit(imagePathList)
            }
        }
    }

    fun coverSelect(
        boardId: Long?,
        cover: Cover,
        selectCover: (Cover) -> Unit
    ) = withIO {
        withSocketState { isConnected: Boolean ->
            imagePathList.value.forEach {
                val imageCover = Cover(CoverType.IMAGE, it)
                createMemberBackgroundUseCase(imageCover, isConnected)
            }

            if (boardId != null) {
                updateBoardUseCase(boardId, cover, isConnected).safeCollect {
                    withMain { selectCover(cover) }
                }
            } else {
                withMain { selectCover(cover) }
            }
        }
    }

    fun addImagePath(imagePath: String) = withIO {
        val list = _imagePathList.value.toMutableList().apply {
            add(imagePath)
        }
        _imagePathList.emit(list)
    }

}