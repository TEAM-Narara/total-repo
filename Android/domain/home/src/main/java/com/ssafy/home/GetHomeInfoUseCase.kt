package com.ssafy.home

import com.ssafy.data.repository.board.BoardRepository
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
) {


    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(isConnected: Boolean): Flow<HomeData> {
        val user = dataStoreRepository.getUser()

        return workspaceRepository.getWorkspaceList(isConnected).flatMapLatest { workspaceList ->
            val selectedWorkspaceFlow = workspaceList.firstOrNull()?.let { workspace ->
                boardRepository.getBoardsByWorkspace(workspace.workSpaceId)
                    .map { boardList ->
                        SelectedWorkSpace(
                            workSpaceId = workspace.workSpaceId,
                            workSpaceName = workspace.name,
                            boards = boardList
                        )
                    }
            } ?: flowOf(SelectedWorkSpace())

            selectedWorkspaceFlow.map { selectedWorkspace ->
                HomeData(
                    user = user,
                    workspaceList = workspaceList,
                    selectedWorkSpace = selectedWorkspace
                )
            }
        }
    }

    suspend operator fun invoke(homeData: HomeData, workspaceId: Long): Flow<HomeData> {
        workspaceStomp.connect(workspaceId)
        return combine(
            workspaceRepository.getWorkspace(workspaceId) ?: throw Exception("존재하지 않는 workspace 입니다."),
            boardRepository.getBoardsByWorkspace(workspaceId)
        ) { workspace, boardList ->
            homeData.copy(
                selectedWorkSpace = SelectedWorkSpace(
                    workSpaceId = workspace?.workSpaceId ?: -1,
                    workSpaceName = workspace?.name ?: "",
                    boards = boardList
                )
            )
        }
    }
}
