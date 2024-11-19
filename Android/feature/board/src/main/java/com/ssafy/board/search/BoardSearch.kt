package com.ssafy.board.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.board.search.dto.SearchAllParameters
import com.ssafy.designsystem.component.CheckBoxGroup
import com.ssafy.designsystem.component.CheckBoxItem
import com.ssafy.designsystem.component.EditableText
import com.ssafy.designsystem.component.IconButton
import com.ssafy.designsystem.values.IconMedium
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.PaddingSmall
import com.ssafy.designsystem.values.White
import com.ssafy.model.search.DueDate

@Composable
fun BoardSearchScreen(
    controller: BoardSearchController,
    popBackToBoardScreen: () -> Unit
) {
    val parameters by controller.searchAllParameters.collectAsStateWithLifecycle()

    BackHandler {
        popBackToBoardScreen()
    }

    BoardSearchScreen(
        parameters = parameters,
        updateSearchText = controller::updateSearchText,
        updateNoMember = controller::updateNoMember,
        updateMember = controller::updateMember,
        updateDueDate = controller::updateDueDate,
        updateNoLabel = controller::updateNoLabel,
        updateLabel = controller::updateLabel,
        popBackToBoardScreen = { popBackToBoardScreen() },
    )
}

@Composable
private fun BoardSearchScreen(
    parameters: SearchAllParameters,
    updateSearchText: (String) -> Unit,
    updateNoMember: () -> Unit,
    updateMember: (Long) -> Unit,
    updateDueDate: (DueDate) -> Unit,
    updateNoLabel: () -> Unit,
    updateLabel: (Long) -> Unit,
    popBackToBoardScreen: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .verticalScroll(scrollState)
            .padding(PaddingDefault)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            IconButton(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                modifier = Modifier.size(IconMedium),
                contentDescription = "뒤로 아이콘",
                onClick = popBackToBoardScreen
            )

            EditableText(
                text = parameters.searchedText,
                onInputFinished = updateSearchText,
                modifier = Modifier
                    .padding(start = PaddingSmall)
                    .weight(1f),
                maxTitleLength = 40,
            )

            IconButton(
                imageVector = Icons.Default.Search,
                contentDescription = "검색",
                onClick = popBackToBoardScreen
            )

        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(PaddingDefault))

        CheckBoxGroup(
            title = "담당자",
            options = parameters.memberMap.toList(),
            onOptionSelected = { updateMember(it.first.memberId) },
            isOptionChecked = { it.second.isSelected },
            noOptionItem = {
                CheckBoxItem(
                    checked = parameters.noMember.second.isSelected,
                    onCheckedChanged = { updateNoMember() },
                ) {
                    OptionText(
                        startIcon = parameters.noMember.second.startIcon,
                        content = parameters.noMember.first,
                    )
                }
            },
            option = { (member, memberInfo) ->
                OptionText(
                    startIcon = memberInfo.startIcon,
                    content = member.nickname,
                )
            }
        )

        Spacer(modifier = Modifier.height(PaddingDefault))

        CheckBoxGroup(
            title = "마감 기한",
            options = parameters.dueDateMap.toList(),
            onOptionSelected = { updateDueDate(it.first) },
            isOptionChecked = { it.second.isSelected },
            option = { (dueDate, dueDateInfo) ->
                OptionText(
                    startIcon = dueDateInfo.startIcon,
                    content = dueDate.toString(),
                )
            }
        )

        Spacer(modifier = Modifier.height(PaddingDefault))

        CheckBoxGroup(
            title = "라벨",
            options = parameters.labelMap.toList(),
            onOptionSelected = { updateLabel(it.first.id) },
            isOptionChecked = { it.second.isSelected },
            noOptionItem = {
                CheckBoxItem(
                    checked = parameters.noLabel.second.isSelected,
                    onCheckedChanged = { updateNoLabel() }
                ) {
                    OptionText(
                        startIcon = parameters.noLabel.second.startIcon,
                        content = parameters.noLabel.first.content,
                        backGroundColor = Color(parameters.noLabel.first.color)
                    )
                }
            },
            option = { (label, labelInfo) ->
                OptionText(
                    startIcon = labelInfo.startIcon,
                    content = label.content,
                    backGroundColor = Color(label.color)
                )
            }
        )
    }
}
