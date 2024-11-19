package com.ssafy.network.source.member

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ssafy.model.member.PageDto
import com.ssafy.model.user.User
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class MemberPagingSource @Inject constructor(
    private val memberDataSource: MemberDataSource,
    private val keyToUrl: (String) -> String,
    private val keyword: String,
    private val sort: List<String>,
    private val filterList: List<Long>,
) : PagingSource<Int, User>() {


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        val page = params.key ?: 1
        val pageDto = PageDto(page, params.loadSize, sort)

        return runCatching {
            memberDataSource.searchMembers(keyword, pageDto).firstOrNull()
        }.fold(
            onSuccess = { response ->
                if (response == null) {
                    return@fold LoadResult.Error(Exception("response is null"))
                }

                val userList: List<User> = response.searchMemberResponseDtoList
                val filteredRepos = userList.filterNot { it.memberId in filterList }.map {

                    val profileUrl = it.profileImgUrl?.let(keyToUrl)
                    it.copy(profileImgUrl = profileUrl)
                }

                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (response.totalPages <= page) null else page + 1
                LoadResult.Page(filteredRepos, prevKey, nextKey)
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
