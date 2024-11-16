package com.ssafy.home

import com.ssafy.data.repository.board.BoardRepository
import com.ssafy.data.repository.member.MemberRepository
import com.ssafy.data.repository.workspace.WorkspaceRepository
import com.ssafy.data.socket.workspace.WorkspaceStomp
import com.ssafy.datastore.DataStoreRepository
import com.ssafy.home.data.HomeData
import com.ssafy.home.data.SelectedWorkSpace
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetHomeInfoUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val workspaceRepository: WorkspaceRepository,
    private val boardRepository: BoardRepository,
    private val workspaceStomp: WorkspaceStomp,
    private val memberRepository: MemberRepository,
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(isConnected: Boolean, workspaceId: Long?): Flow<HomeData> {
        val user = dataStoreRepository.getUser()

        return combine(
            memberRepository.getMember(user.memberId),
            workspaceRepository.getWorkspaceList(isConnected)
        ) { member, workspaceList ->
            member to workspaceList
        }.flatMapLatest { (member, workspaceList) ->
            val selectedWorkSpace = if (workspaceId == null) {
                workspaceList.firstOrNull()
            } else {
                workspaceList.singleOrNull { it.workspaceId == workspaceId }
                    ?: workspaceList.firstOrNull()
            }

            val selectedWorkspaceFlow =
                selectedWorkSpace?.let { workspace ->
                    boardRepository.getBoardsByWorkspace(workspace.workspaceId)
                        .map { boardList ->
                            SelectedWorkSpace(
                                workspaceId = workspace.workspaceId,
                                workspaceName = workspace.name,
                                boards = boardList
                            )
                        }
                } ?: flowOf(SelectedWorkSpace())

            selectedWorkspaceFlow.map { selectedWorkspace ->
                workspaceStomp.connect(selectedWorkspace.workspaceId)
                HomeData(
                    user = member ?: user,
                    workspaceList = workspaceList,
                    selectedWorkSpace = selectedWorkspace
                )
            }

        }
    }

}
