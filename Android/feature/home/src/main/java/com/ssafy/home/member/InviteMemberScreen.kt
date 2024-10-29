package com.ssafy.home.member

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.home.member.components.MemberTopAppBar
import com.ssafy.home.member.data.WorkspaceMemberData
import com.ssafy.home.member.data.SearchMemberData
import com.ssafy.designsystem.R
import com.ssafy.designsystem.component.SearchBar
import com.ssafy.designsystem.component.UserInviteItem
import com.ssafy.designsystem.component.UserSearchItem
import com.ssafy.designsystem.values.Gray
import com.ssafy.designsystem.values.PaddingDefault

@Composable
fun InviteMemberScreen(
    modifier: Modifier = Modifier,
    viewModel: InviteMemberViewModel = hiltViewModel(),
    popBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val workspaceMemberList by viewModel.workspaceMemberList.collectAsStateWithLifecycle()
    val searchMemberList by viewModel.searchMemberList.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            Column {
                MemberTopAppBar(onClosePressed = popBack)
                SearchBar(
                    modifier = Modifier.padding(horizontal = PaddingDefault).padding(bottom = PaddingDefault),
                    onTextChanged = viewModel::searchMember
                )
            }
        }
    ) { paddingValues ->
        workspaceMemberList?.let {
            InviteMemberScreen(
                modifier = Modifier.padding(paddingValues),
                workspaceMemberList = it,
                searchMemberList = searchMemberList,
                onInvite = viewModel::inviteMember,
                onAuthUpdate = viewModel::updateMemberAuth
            )
        }
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Gray.copy(alpha = 0.7f))
        ) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }

    if (uiState.isError && uiState.errorMessage != null) {
        Toast.makeText(
            LocalContext.current,
            uiState.errorMessage,
            Toast.LENGTH_SHORT
        ).show()
    }
}

@Composable
fun InviteMemberScreen(
    modifier: Modifier = Modifier,
    workspaceMemberList: List<WorkspaceMemberData>,
    searchMemberList: List<SearchMemberData>?,
    onInvite: (Long) -> Unit,
    onAuthUpdate: (Long, String) -> Unit
) {
    Column(modifier.fillMaxSize()) {
        searchMemberList?.let {
            if (it.isEmpty()) {
                Text(
                    text = "검색 결과 없음",
                    modifier = Modifier
                        .padding(PaddingDefault)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(it) { member ->
                        UserInviteItem(
                            nickname = member.nickname,
                            email = member.email,
                            onInvite = { onInvite(member.id) },
                            isInvited = member.isInvited
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo_github),
                                contentDescription = ""
                            )
                        }
                    }
                }
            }
        } ?: LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(workspaceMemberList) { member ->
                UserSearchItem(
                    nickname = member.nickname,
                    email = member.email,
                    userAuth = member.auth,
                    onChangeUserAuth = { auth -> onAuthUpdate(member.id, auth) }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_github),
                        contentDescription = ""
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun InviteMemberScreenPreview() {
    InviteMemberScreen(
        workspaceMemberList = (1..10L).map {
            WorkspaceMemberData(
                it,
                nickname = "nickname",
                email = "email",
                auth = "Admin",
            )
        },
        searchMemberList = (1..2L).map {
            SearchMemberData(
                it,
                nickname = "nickname",
                email = "email",
                isInvited = false,
            )
        },
        onInvite = {},
        onAuthUpdate = { _, _ -> }
    )
}