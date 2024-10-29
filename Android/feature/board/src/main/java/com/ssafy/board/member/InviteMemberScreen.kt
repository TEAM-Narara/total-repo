package com.ssafy.board.member

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.board.member.components.MemberTopAppBar
import com.ssafy.board.member.data.BoardMemberData
import com.ssafy.board.member.data.SearchMemberData
import com.ssafy.designsystem.R
import com.ssafy.designsystem.component.SearchBar
import com.ssafy.designsystem.component.UserInviteItem
import com.ssafy.designsystem.component.UserSearchItem
import com.ssafy.designsystem.values.PaddingDefault

@Composable
fun InviteMemberScreen(
    modifier: Modifier = Modifier,
    viewModel: InviteMemberViewModel = hiltViewModel(),
    popBack: () -> Unit
) {
    val boardMemberList by viewModel.boardMemberList.collectAsStateWithLifecycle()
    val searchMemberList by viewModel.searchMemberList.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = { MemberTopAppBar(onClosePressed = popBack) }
    ) { paddingValues ->
        boardMemberList?.let {
            InviteMemberScreen(
                modifier = Modifier.padding(paddingValues),
                boardMemberList = it,
                searchMemberList = searchMemberList,
                onInputChanged = viewModel::searchMember,
                onInvite = viewModel::inviteMember,
                onAuthUpdate = viewModel::updateMemberAuth
            )
        }
    }
}

@Composable
fun InviteMemberScreen(
    modifier: Modifier = Modifier,
    boardMemberList: List<BoardMemberData>,
    searchMemberList: List<SearchMemberData>?,
    onInputChanged: (String) -> Unit,
    onInvite: (Long) -> Unit,
    onAuthUpdate: (Long, String) -> Unit
) {
    Column(modifier.fillMaxSize()) {
        SearchBar(
            modifier = Modifier.padding(horizontal = PaddingDefault),
            onTextChanged = onInputChanged
        )
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
            items(boardMemberList) { member ->
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
        boardMemberList = (1..10L).map {
            BoardMemberData(
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
        onInputChanged = {},
        onInvite = {},
        onAuthUpdate = { _, _ -> }
    )
}