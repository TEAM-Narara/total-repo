package com.ssafy.home.invite

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ssafy.home.GetDetailWorkspaceUseCase
import com.ssafy.home.data.DetailWorkspaceData
import com.ssafy.member.SearchMembersUseCase
import com.ssafy.member.data.UserData
import com.ssafy.member.data.toUser
import com.ssafy.model.member.Authority
import com.ssafy.ui.viewmodel.BaseViewModel
import com.ssafy.workspace.AddWorkspaceMemberUseCase
import com.ssafy.workspace.ChangeWorkspaceMemberUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class InviteWorkspaceViewModel @Inject constructor(
    private val getWorkspaceUseCase: GetDetailWorkspaceUseCase,
    private val searchMemberUseCase: SearchMembersUseCase,
    private val addWorkspaceMemberUseCase: AddWorkspaceMemberUseCase,
    private val changeWorkspaceMemberUseCase: ChangeWorkspaceMemberUseCase,
) : BaseViewModel() {

    private val _workspace = MutableStateFlow(DetailWorkspaceData(-1, "", emptyList()))
    val workspace = _workspace.asStateFlow()

    private val searchParams = MutableStateFlow("")

    @OptIn(FlowPreview::class)
    val searchMember = combine(
        searchParams.debounce(300),
        workspace
    ) { search, workspaceData -> search to workspaceData }
        .flatMapLatest { (search, workspaceData) ->
            if (search.length < 3) {
                flowOf(PagingData.empty())
            } else {
                val workspaceMembers = workspaceData.members.map { it.memberId }
                searchMemberUseCase(search, workspaceMembers)
            }
        }.cachedIn(viewModelScope)

    fun getWorkspace(workspaceId: Long) = viewModelScope.launch {
        getWorkspaceUseCase(workspaceId).safeCollect { it?.let { _workspace.emit(it) } }
    }

    fun setSearchParams(search: String) = searchParams.update { search }

    fun changeAuth(memberId: Long, auth: Authority) = viewModelScope.launch(Dispatchers.IO) {
        val workspaceId = workspace.value.workspaceId
        withSocketState { isConnected ->
            changeWorkspaceMemberUseCase(workspaceId, memberId, auth, isConnected).withUiState()
                .collect()
        }
    }

    fun inviteMember(userData: UserData) = viewModelScope.launch(Dispatchers.IO) {
        val user = userData.toUser()
        addWorkspaceMemberUseCase(workspace.value.workspaceId, user).withUiState().collect()
    }

}
