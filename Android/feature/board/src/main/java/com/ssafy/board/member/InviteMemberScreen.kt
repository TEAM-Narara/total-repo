package com.ssafy.board.member

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import com.ssafy.board.member.components.MemberTopAppBar
import com.ssafy.designsystem.component.SearchBar
import com.ssafy.designsystem.component.UserInviteItem
import com.ssafy.designsystem.component.UserSearchItem
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.member.data.UserData
import com.ssafy.model.board.MemberResponseDTO
import com.ssafy.model.member.Authority
import com.ssafy.ui.uistate.ErrorScreen
import com.ssafy.ui.uistate.LoadingScreen
import com.ssafy.ui.uistate.UiState

@Composable
fun InviteMemberScreen(
    modifier: Modifier = Modifier,
    viewModel: InviteMemberViewModel = hiltViewModel(),
    popBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val boardMembers by viewModel.boardMembers.collectAsStateWithLifecycle()
    val lazyMemberItems = viewModel.searchMember.collectAsLazyPagingItems()

    LaunchedEffect(Unit) { viewModel.resetUiState() }
    InviteMemberScreen(
        modifier = modifier,
        popBack = popBack,
        searchMember = viewModel::searchParams,
        boardMembers = boardMembers,
        lazyMemberItems = lazyMemberItems,
        onInvite = viewModel::inviteMember,
        changeAuth = viewModel::updateMemberAuth
    )


    when (uiState) {
        is UiState.Loading -> LoadingScreen()
        is UiState.Error -> uiState.errorMessage?.let { ErrorScreen(errorMessage = it) }
        is UiState.Success -> {}
        is UiState.Idle -> {}
    }

}

@Composable
private fun InviteMemberScreen(
    modifier: Modifier = Modifier,
    popBack: () -> Unit,
    searchMember: (String) -> Unit,
    boardMembers: List<MemberResponseDTO>?,
    lazyMemberItems: LazyPagingItems<UserData>,
    onInvite: (Long) -> Unit,
    changeAuth: (Long, Authority) -> Unit
) {

    Scaffold(
        modifier = modifier,
        topBar = {
            Column {
                MemberTopAppBar(onClosePressed = popBack)
                SearchBar(
                    modifier = Modifier
                        .padding(horizontal = PaddingDefault)
                        .padding(bottom = PaddingDefault),
                    onTextChanged = searchMember
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            if (lazyMemberItems.itemCount == 0 && boardMembers != null) {
                items(boardMembers.size , key= { boardMembers[it] }){
                    val member = boardMembers[it]
                    UserInviteItem(
                        nickname = member.memberNickname,
                        email = member.memberEmail,
                        onInvite = { },
                        isInvited = true
                    ) {
                        AsyncImage(
                            modifier = Modifier.fillMaxSize(),
                            model = member.memberEmail,
                            contentDescription = null,
                            error = rememberVectorPainter(Icons.Default.AccountCircle)
                        )
                    }
                }
            } else {
                items(lazyMemberItems.itemCount, key = lazyMemberItems.itemKey { it.memberId }) {
                    lazyMemberItems[it]?.let { user ->
                        UserSearchItem(
                            nickname = user.nickname,
                            email = user.email,
                            userAuth = "",
                            onChangeUserAuth = { auth -> changeAuth(user.memberId, Authority.valueOf(auth)) },
                            clickAction = { onInvite(user.memberId) },
                            canChangeAuth = false,
                            icon = {
                                AsyncImage(
                                    modifier = Modifier.fillMaxSize(),
                                    model = user.profileImgUrl,
                                    contentDescription = null,
                                    error = rememberVectorPainter(Icons.Default.AccountCircle)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun InviteMemberScreenPreview() {
    InviteMemberScreen(
       popBack = {}
    )
}
