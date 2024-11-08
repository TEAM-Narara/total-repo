package com.ssafy.home.invite

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ssafy.home.GetDetailWorkspaceUseCase
import com.ssafy.home.data.DetailWorkspaceData
import com.ssafy.member.SearchMembersUseCase
import com.ssafy.member.data.UserData
import com.ssafy.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
) : BaseViewModel() {

    private val _workspace = MutableStateFlow(DetailWorkspaceData(-1, "", emptyList()))
    val workspace = _workspace.asStateFlow()

    private val searchParams = MutableStateFlow("")

    @OptIn(FlowPreview::class)
    val searchMember: Flow<PagingData<UserData>> = searchParams.debounce(300)
        .flatMapLatest { memberData ->
            if (memberData.length < 3) {
                flowOf(PagingData.empty())
            } else {
                val workspaceMembers = workspace.value.members.map { it.memberId }
                searchMemberUseCase(memberData, workspaceMembers)
            }
        }.cachedIn(viewModelScope)


    fun getWorkspace(workspaceId: Long) = viewModelScope.launch {
        getWorkspaceUseCase(workspaceId).safeCollect { it?.let { _workspace.emit(it) } }
    }

    fun setSearchParams(search: String) = searchParams.update { search }

    fun changeAuth(memberId: Long, auth: String) {

    }

    fun inviteMember(memberId: Long) {

    }

}