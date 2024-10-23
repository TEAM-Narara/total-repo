package com.ssafy.home.createboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.ssafy.designsystem.component.EditText
import com.ssafy.designsystem.values.PaddingDefault

@Composable
fun CreateBoardBody(
    modifier: Modifier = Modifier,
    board: Any,
    workSpaceList: List<Any>,
    background: Any,
    moveToSelectBackgroundScreen: () -> Unit
) {
    val scrollState = rememberScrollState()
    val (boardName, setBoardName) = remember { mutableStateOf(board.toString()) }
    val (selectedWorkSpace, setSelectedWorkSpace) = remember { mutableStateOf(workSpaceList[0].toString()) }

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(PaddingDefault)
    ) {
        EditText(
            title = "보드명",
            text = boardName,
            onTextChange = setBoardName
        )

//        EditText(
//            title = "워크 스페이스",
//            enabled = false,
//            isTextFieldClickable = true,
//            onTextFieldClick = { /*워크 스페이스 선택 공간*/ },
//            text = selectedWorkSpace,
//            onTextChange = setSelectedWorkSpace,
//            icon = Icons.Default.ArrowDropDown
//        )
    }
}