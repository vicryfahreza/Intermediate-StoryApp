package com.vicryfahreza.storyapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.vicryfahreza.storyapp.model.UserPreference
import com.vicryfahreza.storyapp.response.ListStoryItem
import com.vicryfahreza.storyapp.service.ApiService
import kotlinx.coroutines.flow.first
import java.lang.Exception

class StoryPaging(private val apiService: ApiService, private val pref: UserPreference): PagingSource<Int, ListStoryItem>() {

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(ONE_VALUE_REFRESHED) ?: anchorPage?.nextKey?.minus(ONE_VALUE_REFRESHED)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val page = params.key ?: INDEX_OF_PAGE
            val name = pref.getUser().first()
            val responseData = apiService.getAllStories("Bearer $name", page, params.loadSize).listStory


            LoadResult.Page(
                data = responseData,
                if (page == INDEX_OF_PAGE) null else page - 1,
                if (responseData.isEmpty()) null else page + 1
            )
        } catch (e: Exception){
            return LoadResult.Error(e)
        }
    }

    private companion object {
        const val INDEX_OF_PAGE = 1
        const val ONE_VALUE_REFRESHED = 1
    }

}