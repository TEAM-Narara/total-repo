package com.ssafy.card.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.card.card.component.CardBottomBar
import com.ssafy.card.card.component.CardTopBar
import com.ssafy.card.card.component.cardAttachmentInfo
import com.ssafy.card.card.component.cardComment
import com.ssafy.card.card.component.cardInfoScreen
import com.ssafy.card.card.component.cardMemberInfo
import com.ssafy.designsystem.values.DividerLarge
import com.ssafy.designsystem.values.Gray
import com.ssafy.designsystem.values.IconLegendLarge
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.PaddingLegendLarge
import com.ssafy.model.card.CardDTO
import com.ssafy.model.card.CommentDTO
import com.ssafy.ui.launcher.rememberLauncherForSaveImage

@Composable
fun CardScreen(
    viewModel: CardViewModel = hiltViewModel(),
    popBackToBoardScreen: () -> Unit,
    moveToSelectLabel: () -> Unit
) {
    val cardDTO by viewModel.cardDTO.collectAsStateWithLifecycle()
    val userId by viewModel.userId.collectAsStateWithLifecycle()

    CardScreen(
        cardDTO = cardDTO,
        userId = userId,
        moveToSelectLabel = moveToSelectLabel,
        popBackToBoardScreen = popBackToBoardScreen,
        moveToArchive = { viewModel.moveToArchive(popBackToBoardScreen) },
        moveToDelete = { viewModel.moveToDelete(popBackToBoardScreen) },
        setCardWatching = { isWatching -> viewModel.setCardWatching(isWatching) },
        setCardContent = { content -> viewModel.setCardContent(content) },
        saveCardContent = { viewModel.saveCardContent() },
        resetCardContent = { viewModel.resetCardContent() },
        deleteComment = { comment -> viewModel.deleteComment(comment) },
        addAttachment = { filePath -> viewModel.addAttachment(filePath) },
        addComment = { comment -> viewModel.addComment(comment) },
        setCommitContent = { comment, content -> viewModel.setCommitContent(comment, content) },
        saveCommitContent = { comment -> viewModel.saveCommitContent(comment) },
        resetCommitContent = { comment -> viewModel.resetCommitContent(comment) }
    )
}

@Composable
private fun CardScreen(
    cardDTO: CardDTO,
    userId: Long,
    popBackToBoardScreen: () -> Unit,
    moveToSelectLabel: () -> Unit,
    moveToArchive: () -> Unit,
    moveToDelete: () -> Unit,
    setCardWatching: (Boolean) -> Unit,
    setCardContent: (String) -> Unit,
    saveCardContent: () -> Unit,
    resetCardContent: () -> Unit,
    deleteComment: (CommentDTO) -> Unit,
    addAttachment: (String) -> Unit,
    addComment: (String) -> Unit,
    setCommitContent: (CommentDTO, String) -> Unit,
    saveCommitContent: (CommentDTO) -> Unit,
    resetCommitContent: (CommentDTO) -> Unit
) {

    val (isContentFocus, setContentFocus) = remember { mutableStateOf(false) }
    val (focusedComment, setFocusedComment) = remember { mutableStateOf<CommentDTO?>(null) }
    val attachmentLauncher = rememberLauncherForSaveImage { filePath ->
        addAttachment(filePath)
    }

    // 스크롤 상태를 저장할 상태값 추가
    val heightOffset = remember { mutableFloatStateOf(0f) }
    // 최대 스크롤 가능한 높이 설정
    val maxOffset = with(LocalDensity.current) { IconLegendLarge.toPx() }

    // NestedScrollConnection 수정
    val lazyListState = rememberLazyListState()
    val connection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val isListAtTop = lazyListState.firstVisibleItemIndex == 0

                return if (delta > 0) { // 아래로 스크롤 (offset 증가)
                    if (isListAtTop && heightOffset.floatValue < 0f) {
                        // 리스트가 최상단이고 offset이 0보다 작을 때만 이미지 크기 증가
                        val oldOffset = heightOffset.floatValue
                        val newOffset = (oldOffset + delta).coerceIn(-maxOffset, 0f)
                        heightOffset.floatValue = newOffset
                        Offset(0f, newOffset - oldOffset)
                    } else {
                        Offset.Zero
                    }
                } else { // 위로 스크롤 (offset 감소)
                    val oldOffset = heightOffset.floatValue
                    val newOffset = (oldOffset + delta).coerceIn(-maxOffset, 0f)
                    heightOffset.floatValue = newOffset
                    if (oldOffset > -maxOffset) {
                        Offset(0f, newOffset - oldOffset)
                    } else {
                        Offset.Zero
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.nestedScroll(connection)) {
        Scaffold(
            topBar = {
                CardTopBar(
                    onBackPressed = popBackToBoardScreen,
                    onWatchSelected = setCardWatching,
                    isWatching = cardDTO.isWatching,
                    moveToArchive = moveToArchive,
                    moveToDelete = moveToDelete,
                    attachments = cardDTO.attachments,
                    heightOffset = heightOffset.floatValue
                )
            },
            bottomBar = {
                CardBottomBar(
                    isContentFocus = isContentFocus,
                    setContentFocus = setContentFocus,
                    focusedComment = focusedComment,
                    setFocusedComment = setFocusedComment,
                    saveCardContent = saveCardContent,
                    resetCardContent = resetCardContent,
                    saveCommitContent = { focusedComment?.let(saveCommitContent) },
                    resetCommitContent = { focusedComment?.let(resetCommitContent) },
                    addComment = addComment
                )
            },
        ) { innerPadding ->

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding),
                contentPadding = PaddingValues(bottom = PaddingLegendLarge),
                verticalArrangement = Arrangement.spacedBy(PaddingDefault),
                state = lazyListState,
            ) {
                cardInfoScreen(
                    modifier = Modifier.padding(horizontal = PaddingDefault),
                    cardDTO = cardDTO,
                    onClickLabel = moveToSelectLabel,
                    onClickDate = { /* TODO 날짜 선택 다이얼로그 호출 */ },
                    isContentFocus = isContentFocus,
                    setContentFocus = setContentFocus,
                    setContent = setCardContent
                )

                item(key = "divider1") {
                    HorizontalDivider(
                        thickness = DividerLarge,
                        color = Gray
                    )
                }

                cardMemberInfo(
                    modifier = Modifier.padding(horizontal = PaddingDefault),
                    members = cardDTO.members,
                    showCardMembers = {
                        // TODO 카드 멤버 호출하는 다이얼로그
                    }
                )

                item(key = "divider2") {
                    HorizontalDivider(
                        thickness = DividerLarge,
                        color = Gray
                    )
                }

                cardAttachmentInfo(
                    modifier = Modifier.padding(horizontal = PaddingDefault),
                    attachments = cardDTO.attachments,
                    addPhoto = { attachmentLauncher.launch("image/*") }
                )

                item(key = "divider3") {
                    HorizontalDivider(
                        thickness = DividerLarge,
                        color = Gray
                    )
                }

                cardComment(
                    modifier = Modifier.padding(horizontal = PaddingDefault),
                    comments = cardDTO.comments,
                    userId = userId,
                    setCommitContent = setCommitContent,
                    deleteComment = deleteComment,
                    focusedComment = focusedComment,
                    setFocusedComment = setFocusedComment
                )
            }
        }
    }
}

@Composable
@Preview
fun CardScreenPreview() {
    CardScreen(
        cardDTO = CardDTO(),
        userId = 0,
        popBackToBoardScreen = { },
        moveToSelectLabel = { },
        moveToArchive = { },
        moveToDelete = { },
        setCardWatching = { },
        setCardContent = { },
        saveCardContent = { },
        resetCardContent = { },
        deleteComment = { },
        addAttachment = { },
        addComment = { },
        setCommitContent = { _, _ -> },
        saveCommitContent = { },
        resetCommitContent = { }
    )
}
