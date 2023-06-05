package com.vicryfahreza.storyapp.data.paging

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.vicryfahreza.storyapp.model.UserPreference
import com.vicryfahreza.storyapp.response.ListStoryItem
import com.vicryfahreza.storyapp.service.ApiService

class PreferenceStoryRepo(private val apiService: ApiService, private val pref: UserPreference) {

    fun getPrefStory(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPaging(apiService, pref)
            }
        ).liveData
    }
}