package com.ssafy.home.invite

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import com.ssafy.designsystem.component.SearchBar
import com.ssafy.designsystem.component.UserInviteItem
import com.ssafy.designsystem.component.UserSearchItem
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.PaddingSmall
import com.ssafy.designsystem.values.Primary
import com.ssafy.designsystem.values.White
import com.ssafy.home.data.DetailWorkspaceData
import com.ssafy.member.data.UserData
import com.ssafy.model.member.Authority
import com.ssafy.ui.uistate.ErrorScreen
import com.ssafy.ui.uistate.LoadingScreen
import com.ssafy.ui.uistate.UiState

@Composable
fun InviteWorkspaceScreen(
    viewModel: InviteWorkspaceViewModel = hiltViewModel(),
    workspaceId: Long,
    popBackToHome: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val workspace by viewModel.workspace.collectAsStateWithLifecycle()
    val lazyMemberItems = viewModel.searchMember.collectAsLazyPagingItems()

    LaunchedEffect(Unit) {
        viewModel.resetUiState()
        viewModel.getWorkspace(workspaceId)
    }

    InviteWorkspaceScreen(
        workspace = workspace,
        lazyMemberItems = lazyMemberItems,
        popBackToHome = popBackToHome,
        setSearchParams = viewModel::setSearchParams,
        changeAuth = viewModel::changeAuth,
        onInvite = viewModel::inviteMember
    )

    when (uiState) {
        is UiState.Loading -> LoadingScreen()
        is UiState.Error -> uiState.errorMessage?.let { ErrorScreen(errorMessage = it) }
        is UiState.Success -> {}
        is UiState.Idle -> {}
    }

}

@Composable
private fun InviteWorkspaceScreen(
    workspace: DetailWorkspaceData,
    lazyMemberItems: LazyPagingItems<UserData>,
    popBackToHome: () -> Unit,
    setSearchParams: (String) -> Unit,
    changeAuth: (Long, Authority) -> Unit,
    onInvite: (UserData) -> Unit
) {
    val activity = LocalContext.current as? Activity
    activity?.let {
        WindowCompat.getInsetsController(it.window, it.window.decorView).apply {
            isAppearanceLightStatusBars = false
            it.window.statusBarColor = Primary.toArgb()
        }
    }

    Scaffold(
        containerColor = White,
        topBar = {
            Column {
                InviteTopBar(onClosePressed = popBackToHome)
                SearchBar(
                    modifier = Modifier
                        .padding(horizontal = PaddingDefault)
                        .padding(bottom = PaddingDefault),
                    onTextChanged = setSearchParams
                )
            }
        }
    ) { innerPadding ->

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(PaddingSmall),
            modifier = Modifier
                .padding(innerPadding)
                .padding(PaddingDefault)
        ) {

            if (lazyMemberItems.itemCount == 0) {
                items(workspace.members.size, key = { workspace.members[it].memberId }) {
                    val member = workspace.members[it]
                    UserSearchItem(
                        nickname = member.memberNickname,
                        email = member.memberEmail,
                        userAuth = member.authority.name,
                        onChangeUserAuth = { auth ->
                            changeAuth(
                                member.memberId,
                                Authority.valueOf(auth)
                            )
                        },
                        canChangeAuth = true,
                        icon = {
                            AsyncImage(
                                modifier = Modifier.fillMaxSize(),
                                model = member.memberProfileImgUrl,
                                contentDescription = null,
                                error = rememberVectorPainter(Icons.Default.AccountCircle)
                            )
                        }
                    )
                }
            } else {
                items(lazyMemberItems.itemCount, key = lazyMemberItems.itemKey { it.memberId }) {
                    lazyMemberItems[it]?.let { user ->
                        UserInviteItem(
                            nickname = user.nickname,
                            email = user.email,
                            onInvite = { onInvite(user) },
                            isInvited = true
                        ) {
                            AsyncImage(
                                modifier = Modifier.fillMaxSize(),
                                model = user.profileImgUrl,
                                contentDescription = null,
                                error = rememberVectorPainter(Icons.Default.AccountCircle)
                            )
                        }
                    }
                }
            }
        }
    }

}

@Composable
@Preview
private fun InviteWorkspaceScreenPreview() {
    InviteWorkspaceScreen(
        workspaceId = 1,
        popBackToHome = {}
    )

}
