package com.ssafy.home

import com.ssafy.data.repository.board.BoardRepository
import com.ssafy.data.repository.workspace.WorkspaceRepository
import com.ssafy.datastore.DataStoreRepository
import com.ssafy.home.data.HomeData
import com.ssafy.model.user.User
import com.ssafy.model.workspace.WorkSpaceDTO
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
    private val boardRepository: BoardRepository
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(isConnected: Boolean): Flow<HomeData> {
        val userFlow = flowOf(dataStoreRepository.getUser())

        return combine(
            userFlow,
            workspaceRepository.getWorkspaceList(isConnected)
        ) { user, workspaceList ->
            user to workspaceList
        }.flatMapLatest { (user: User, workspaceList: List<WorkSpaceDTO>) ->
            workspaceList.firstOrNull()?.let { workspace ->
                boardRepository.getBoardsByWorkspace(workspace.workSpaceId).map { boardList ->
                    HomeData(user, workspaceList, boardList)
                }
            } ?: flowOf(HomeData(user, workspaceList, emptyList()))
        }
    }

    suspend operator fun invoke(homeData: HomeData, workspaceId: Long): Flow<HomeData> {
        return boardRepository.getBoardsByWorkspace(workspaceId).map { boardList ->
            homeData.copy(boardsBySelectedWorkSpace = boardList)
        }

    }
}
