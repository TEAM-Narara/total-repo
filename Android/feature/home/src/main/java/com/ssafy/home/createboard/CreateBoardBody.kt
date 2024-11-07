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
import com.ssafy.model.background.CoverType
import com.ssafy.model.board.BoardDTO
import com.ssafy.model.board.Visibility
import com.ssafy.model.workspace.WorkSpaceDTO

@Composable
fun CreateBoardBody(
    modifier: Modifier = Modifier,
    workSpaceList: List<WorkSpaceDTO>,
    cover: Cover,
    moveToSelectBackgroundScreen: (Cover?) -> Unit,
    createBoardClick: (boardDTO: BoardDTO) -> Unit
) {
    val scrollState = rememberScrollState()
    val (boardName, setBoardName) = remember { mutableStateOf("") }
    val (workSpace, setWorkSpace) = remember { mutableStateOf(workSpaceList.firstOrNull()) }
    val (visibleScope, setVisibleScope) = remember { mutableStateOf(Visibility.WORKSPACE) }

    Column(
        verticalArrangement = Arrangement.spacedBy(PaddingDefault),
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(PaddingDefault)
    ) {
        EditText(
            title = "보드명",
            text = boardName,
            onTextChange = setBoardName,
            textColor = Primary
        )

        DropDownText(
            title = "워크 스페이스",
            dropdownList = workSpaceList,
            initItem = workSpace,
            onItemChange = setWorkSpace,
            dropdownItemToText = { it?.name ?: "" }
        )

        DropDownText(
            title = "공개 범위",
            dropdownList = Visibility.entries,
            initItem = visibleScope,
            onItemChange = setVisibleScope,
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

            when (cover.type) {
                CoverType.COLOR -> {
                    Image(
                        painter = ColorPainter(cover.value.toColor()),
                        contentDescription = "Color",
                        modifier = Modifier
                            .size(BackgroundMini)
                            .clickable { moveToSelectBackgroundScreen(cover) }
                    )
                }

                CoverType.IMAGE -> {
                    AsyncImage(
                        model = cover.value,
                        contentDescription = "Image",
                        modifier = Modifier
                            .size(BackgroundMini)
                            .clickable { moveToSelectBackgroundScreen(cover) }
                    )
                }

                CoverType.NONE -> {
                    Image(
                        painter = ColorPainter(Yellow),
                        contentDescription = "Color",
                        modifier = Modifier
                            .size(BackgroundMini)
                            .clickable { moveToSelectBackgroundScreen(cover) }
                    )
                }
            }
        }

        HorizontalDivider(color = LightGray)

        if (workSpace != null) {
            FilledButton(text = "보드 생성", onClick = {
                val boardDTO = BoardDTO(
                    id = 0,
                    workspaceId = workSpace.workSpaceId,
                    name = boardName,
                    cover = cover,
                    isClosed = false,
                    visibility = visibleScope
                )
                createBoardClick(boardDTO)
            })
        }
    }
}
