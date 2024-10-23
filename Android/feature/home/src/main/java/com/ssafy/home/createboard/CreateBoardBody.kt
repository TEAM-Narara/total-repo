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
import com.ssafy.designsystem.component.DropDownText
import com.ssafy.designsystem.component.EditText
import com.ssafy.designsystem.component.FilledButton
import com.ssafy.designsystem.values.BackgroundMini
import com.ssafy.designsystem.values.LightGray
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.PaddingSmall
import com.ssafy.designsystem.values.Pink
import com.ssafy.designsystem.values.Primary
import com.ssafy.designsystem.values.TextLarge
import com.ssafy.designsystem.visibleList

@Composable
fun CreateBoardBody(
    modifier: Modifier = Modifier,
    board: Any,
    workSpaceList: List<Any>,
    background: Any,
    moveToSelectBackgroundScreen: () -> Unit,
    createBoardClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val (boardName, setBoardName) = remember { mutableStateOf(board.toString()) }
    val (workSpace, setWorkSpace) = remember { mutableStateOf(workSpaceList.first().toString()) }
    val (visibleScope, setVisibleScope) = remember { mutableStateOf(visibleList.first()) }

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
            dropdownList = workSpaceList.map { it.toString() },
            initItem = workSpace,
            onItemChange = setWorkSpace,
        )

        DropDownText(
            title = "공개 범위",
            dropdownList = visibleList,
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

            // TODO 백그라운드 이미지를 선택할 수 있게 변경
            Image(
                painter = ColorPainter(Pink), contentDescription = null,
                modifier = Modifier
                    .size(BackgroundMini)
                    .clickable { moveToSelectBackgroundScreen() }
            )
        }

        HorizontalDivider(color = LightGray)

        FilledButton(text = "보드 생성", onClick = { createBoardClick() })
    }
}