package com.ssafy.network.source.board

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ssafy.model.activity.BoardActivity
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class BoardActivityPagingSource @Inject constructor(
    private val boardId: Long,
    private val boardDataSource: BoardDataSource
) : PagingSource<Int, BoardActivity>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BoardActivity> {
        val page = params.key ?: 1

        return runCatching {
            boardDataSource.getBoardActivity(boardId, page, params.loadSize).firstOrNull()
        }.fold(
            onSuccess = { response ->

                if (response == null) {
                    return@fold LoadResult.Error(Exception("response is null"))
                }

                val activity = response.boardActivityList
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (response.totalPages <= page) null else page + 1
                LoadResult.Page(activity, prevKey, nextKey)
            },
            onFailure = { exception ->
                Log.e("BoardActivityPagingSource", "load: $exception")
                LoadResult.Error(exception)
            }
        )
    }


    override fun getRefreshKey(state: PagingState<Int, BoardActivity>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val closestPage = state.closestPageToPosition(anchorPosition)
            closestPage?.prevKey?.plus(1) ?: closestPage?.nextKey?.minus(1)
        }

    companion object {
        const val PAGE_SIZE = 30
    }
}