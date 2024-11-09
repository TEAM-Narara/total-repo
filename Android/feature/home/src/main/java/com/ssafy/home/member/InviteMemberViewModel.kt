package com.ssafy.home.member

import androidx.lifecycle.viewModelScope
import com.ssafy.home.member.data.WorkspaceMemberData
import com.ssafy.home.member.data.SearchMemberData
import com.ssafy.socket.GetSocketStateUseCase
import com.ssafy.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class InviteMemberViewModel @Inject constructor(
    getSocketStateUseCase: GetSocketStateUseCase,
) : BaseViewModel(getSocketStateUseCase) {
    private val _workspaceId: MutableStateFlow<Long?> = MutableStateFlow(null)
    fun setWorkspaceId(boardId: Long) = _workspaceId.update { boardId }

    private val searchMemberText: MutableStateFlow<String> = MutableStateFlow("")

    val workspaceMemberList = _workspaceId.filterNotNull().flatMapLatest {
        flow {
            emit((0L..10L).map {
                WorkspaceMemberData(
                    it,
                    nickname = "nickname",
                    email = "email",
                    auth = "Admin",
                )
            })
        }.withUiState()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    val searchMemberList = searchMemberText.filterNotNull().flatMapLatest { inputText ->
        flow {
            if (inputText.isEmpty()) emit(null)
            else emit(
                listOf(
                    SearchMemberData(
                        0L,
                        nickname = "nickname",
                        email = "email",
                        isInvited = true
                    ),
                    SearchMemberData(
                        1L,
                        nickname = "nickname",
                        email = "email",
                        isInvited = false
                    )
                )
            )
        }.withUiState()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    fun searchMember(input: String) {
        searchMemberText.update { input }
    }

    fun inviteMember(id: Long) {}

    fun updateMemberAuth(id: Long, auth: String) {}
}