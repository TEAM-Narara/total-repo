package com.ssafy.home.createboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.ssafy.designsystem.component.DropDownText
import com.ssafy.designsystem.component.EditText
import com.ssafy.designsystem.component.FilledButton
import com.ssafy.designsystem.values.BackgroundMini
import com.ssafy.designsystem.values.LightGray
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.PaddingSmall
import com.ssafy.designsystem.values.Primary
import com.ssafy.designsystem.values.TextLarge
import com.ssafy.designsystem.values.Yellow
import com.ssafy.designsystem.values.toColor
import com.ssafy.model.background.Cover
import com.ssafy.model.board.BoardDTO
import com.ssafy.model.board.Visibility
import com.ssafy.model.with.CoverType
import com.ssafy.model.workspace.WorkSpaceDTO

@Composable
fun CreateBoardBody(
    modifier: Modifier = Modifier,
    workSpaceList: List<WorkSpaceDTO>,
    boardData: BoardDTO,
    changeBoardName: (String) -> Unit,
    changeWorkspace: (WorkSpaceDTO) -> Unit,
    changeVisibleScope: (Visibility) -> Unit,
    moveToSelectBackgroundScreen: (Cover) -> Unit,
    createBoardClick: () -> Unit,
) {
    val scrollState = rememberScrollState()
    val (boardName, setBoardName) = remember(boardData.name) { mutableStateOf(boardData.name) }
    val (workSpace, setWorkSpace) = remember(boardData.workspaceId) {
        mutableStateOf(workSpaceList.find { it.workspaceId == boardData.workspaceId })
    }
    val (visibleScope, setVisibleScope) = remember(boardData.visibility) {
        mutableStateOf(boardData.visibility)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(PaddingDefault),
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(PaddingDefault)
    ) {
        EditText(
            title = "보드명",
            text = boardName,
            onTextChange = {
                setBoardName(it)
                changeBoardName(it)
            },
            textColor = Primary
        )

        DropDownText(
            title = "워크 스페이스",
            dropdownList = workSpaceList,
            initItem = workSpace,
            onItemChange = {
                setWorkSpace(it)
                if (it != null) changeWorkspace(it)
            },
            dropdownItemToText = { it?.name ?: "" }
        )

        DropDownText(
            title = "공개 범위",
            dropdownList = Visibility.entries,
            initItem = visibleScope,
            onItemChange = {
                setVisibleScope(it)
                changeVisibleScope(it)
            },
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = PaddingSmall)
        ) {
            Text(
                text = "Background",
                modifier = Modifier.weight(1f),
                fontSize = TextLarge,
                color = Primary
            )

            when (boardData.cover.type) {
                CoverType.COLOR -> {
                    Image(
                        painter = ColorPainter(boardData.cover.value.toColor()),
                        contentDescription = "Color",
                        modifier = Modifier
                            .size(BackgroundMini)
                            .clickable { moveToSelectBackgroundScreen(boardData.cover) }
                    )
                }

                CoverType.IMAGE -> {
                    AsyncImage(
                        model = boardData.cover.value,
                        contentDescription = "Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(BackgroundMini)
                            .clickable { moveToSelectBackgroundScreen(boardData.cover) }
                    )
                }

                CoverType.NONE -> {
                    Image(
                        painter = ColorPainter(Yellow),
                        contentDescription = "Color",
                        modifier = Modifier
                            .size(BackgroundMini)
                            .clickable { moveToSelectBackgroundScreen(boardData.cover) }
                    )
                }
            }
        }

        HorizontalDivider(color = LightGray)

        if (workSpace != null) {
            FilledButton(text = "보드 생성", onClick = createBoardClick)
        }
    }
}
