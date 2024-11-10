package com.ssafy.board.member

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ssafy.board.GetBoardMembersUseCase
import com.ssafy.member.SearchMembersUseCase
import com.ssafy.member.data.UserData
import com.ssafy.model.member.Authority
import com.ssafy.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class InviteMemberViewModel @Inject constructor(
    private val getBoardMemberUseCase: GetBoardMembersUseCase,
    private val searchMemberUseCase: SearchMembersUseCase,
) : BaseViewModel() {
    private val _boardId: MutableStateFlow<Long?> = MutableStateFlow(null)
    fun setBoardId(boardId: Long) = _boardId.update { boardId }

    private val searchMemberText: MutableStateFlow<String> = MutableStateFlow("")

    val boardMembers = _boardId.filterNotNull().flatMapLatest { boardId ->
        getBoardMemberUseCase(boardId)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    @OptIn(FlowPreview::class)
    val searchMember: Flow<PagingData<UserData>> = searchMemberText.debounce(300)
        .flatMapLatest { memberData: String ->
            if (memberData.length < 3) {
                flowOf(PagingData.empty())
            } else {
                val workspaceMembers = boardMembers.value?.map { it.memberId } ?: emptyList()
                searchMemberUseCase(memberData, workspaceMembers)
            }
        }.cachedIn(viewModelScope)


    fun searchParams(input: String) = searchMemberText.update { input }

    fun inviteMember(memberId: Long) {

    }

    fun updateMemberAuth(memberId: Long, auth: Authority) {

    }

}
