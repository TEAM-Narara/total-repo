package com.ssafy.network.source.member

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ssafy.model.member.PageDto
import com.ssafy.model.user.User
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class MemberPagingSource @Inject constructor(
    private val memberDataSource: MemberDataSource,
    private val keyword: String,
    private val sort: List<String>,
) : PagingSource<Int, User>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        val page = params.key ?: 0
        val pageDto = PageDto(page, params.loadSize, sort)

        return runCatching {
            memberDataSource.searchMembers(keyword, pageDto).firstOrNull()
        }.fold(
            onSuccess = { response ->
                val repos: List<User> = response ?: emptyList()
                val nextKey = if (repos.isEmpty()) null else page + (params.loadSize / PAGE_SIZE)
                val prevKey = if (page == 0) null else page - 1
                LoadResult.Page(repos, prevKey, nextKey)
            },
            onFailure = { exception ->
                LoadResult.Error(exception)
            }
        )
    }

    override fun getRefreshKey(state: PagingState<Int, User>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val closestPage = state.closestPageToPosition(anchorPosition)
            closestPage?.prevKey?.plus(1) ?: closestPage?.nextKey?.minus(1)
        }


    companion object {
        const val PAGE_SIZE = 30
    }
}
