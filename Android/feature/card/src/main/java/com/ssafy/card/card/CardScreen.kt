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
import com.ssafy.card.member.dialogs.ModifyManagerDialog
import com.ssafy.card.period.data.PeriodData
import com.ssafy.card.period.dialogs.PeriodDialog
import com.ssafy.designsystem.dialog.rememberDialogState
import com.ssafy.designsystem.values.DividerLarge
import com.ssafy.designsystem.values.Gray
import com.ssafy.designsystem.values.IconLegendLarge
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.PaddingLegendLarge
import com.ssafy.model.card.CardDTO
import com.ssafy.model.card.CommentDTO
import com.ssafy.ui.launcher.rememberLauncherForSaveImage
import com.ssafy.ui.uistate.ErrorScreen
import com.ssafy.ui.uistate.LoadingScreen
import com.ssafy.ui.uistate.UiState

@Composable
fun CardScreen(
    viewModel: CardViewModel = hiltViewModel(),
    popBackToBoardScreen: () -> Unit,
    moveToSelectLabel: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cardDTO by viewModel.cardDTO.collectAsStateWithLifecycle()
    val userId by viewModel.userId.collectAsStateWithLifecycle()
    val memberList by viewModel.careMemberList.collectAsStateWithLifecycle()

    val managerDialogState = rememberDialogState<Unit>()
    val periodDialogState = rememberDialogState<PeriodData>()

    cardDTO?.let {
        CardScreen(
            cardDTO = it,
            userId = userId ?: -1,
            moveToSelectLabel = moveToSelectLabel,
            popBackToBoardScreen = popBackToBoardScreen,
            moveToArchive = { viewModel.moveToArchive(popBackToBoardScreen) },
            moveToDelete = { viewModel.moveToDelete(popBackToBoardScreen) },
            setCardWatching = { isWatching -> viewModel.setCardWatching(isWatching) },
            setCardTitle = { title -> viewModel.setCardTitle(title) },
            setCardContent = { content -> viewModel.setCardContent(content) },
            saveCardTitle = { viewModel.saveCardTitle() },
            saveCardContent = { viewModel.saveCardContent() },
            resetCardTitle = { viewModel.resetCardTitle() },
            resetCardContent = { viewModel.resetCardContent() },
            deleteComment = { comment -> viewModel.deleteComment(comment) },
            addAttachment = { filePath -> viewModel.addAttachment(filePath) },
            deleteAttachment = { id -> viewModel.deleteAttachment(id) },
            updateAttachmentToCover = { id -> viewModel.updateAttachmentToCover(id) },
            addComment = { comment -> viewModel.addComment(comment) },
            setCommitContent = { comment, content -> viewModel.setCommitContent(comment, content) },
            saveCommitContent = { comment -> viewModel.saveCommitContent(comment) },
            resetCommitContent = { comment -> viewModel.resetCommitContent(comment) },
            showPeriod = { periodDialogState.show(PeriodData(it.startDate, it.endDate)) },
            showCardMembers = { managerDialogState.show() }
        )
    }

    ModifyManagerDialog(
        dialogState = managerDialogState,
        onIsManagerChanged = viewModel::toggleIsManager,
        memberList = memberList ?: emptyList()
    )

    PeriodDialog(
        dialogState = periodDialogState,
        onConfirm = viewModel::updatePeriod
    )


    when (uiState) {
        is UiState.Loading -> LoadingScreen()
        is UiState.Error -> uiState.errorMessage?.let { ErrorScreen(errorMessage = it) }
        is UiState.Success -> {}
        is UiState.Idle -> {}
    }
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
    setCardTitle: (String) -> Unit,
    setCardContent: (String) -> Unit,
    saveCardTitle: () -> Unit,
    saveCardContent: () -> Unit,
    resetCardContent: () -> Unit,
    resetCardTitle: () -> Unit,
    deleteComment: (CommentDTO) -> Unit,
    addAttachment: (String) -> Unit,
    deleteAttachment: (Long) -> Unit,
    updateAttachmentToCover: (Long) -> Unit,
    addComment: (String) -> Unit,
    setCommitContent: (CommentDTO, String) -> Unit,
    saveCommitContent: (CommentDTO) -> Unit,
    resetCommitContent: (CommentDTO) -> Unit,
    showPeriod: () -> Unit,
    showCardMembers: () -> Unit
) {

    val (isTitleFocus, setTitleFocus) = remember { mutableStateOf(false) }
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
                    cover = cardDTO.cover,
                    heightOffset = heightOffset.floatValue
                )
            },
            bottomBar = {
                CardBottomBar(
                    isTitleFocus = isTitleFocus,
                    setTitleFocus = setTitleFocus,
                    isContentFocus = isContentFocus,
                    setContentFocus = setContentFocus,
                    focusedComment = focusedComment,
                    setFocusedComment = setFocusedComment,
                    saveCardTitle = saveCardTitle,
                    saveCardContent = saveCardContent,
                    resetCardTitle = resetCardTitle,
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
                    onClickDate = showPeriod,
                    isTitleFocus = isTitleFocus,
                    setTitleFocus = setTitleFocus,
                    setTitle = setCardTitle,
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
                    showCardMembers = showCardMembers
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
                    addPhoto = { attachmentLauncher.launch("image/*") },
                    deleteAttachment = deleteAttachment,
                    updateAttachmentToCover = updateAttachmentToCover
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
        setCardTitle = { },
        setCardContent = { },
        saveCardTitle = { },
        saveCardContent = { },
        resetCardContent = { },
        resetCardTitle = { },
        deleteComment = { },
        addAttachment = { },
        addComment = { },
        setCommitContent = { _, _ -> },
        saveCommitContent = { },
        resetCommitContent = { },
        showPeriod = { },
        showCardMembers = { },
        updateAttachmentToCover = { },
        deleteAttachment = { },
    )
}
