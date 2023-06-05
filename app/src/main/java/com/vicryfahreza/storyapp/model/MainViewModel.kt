package com.vicryfahreza.storyapp.model

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.vicryfahreza.storyapp.data.paging.PreferenceStoryRepo
import com.vicryfahreza.storyapp.response.ListStoryItem
import kotlinx.coroutines.launch



class MainViewModel(private val pref: UserPreference, prefStoryRepo : PreferenceStoryRepo ) : ViewModel(){

    val storyPaging: LiveData<PagingData<ListStoryItem>> =
        prefStoryRepo.getPrefStory().cachedIn(viewModelScope)

    fun getUser(): LiveData<String> {
        return pref.getUser().asLiveData()
    }

    fun saveUser(userToken: String) {
        viewModelScope.launch {
            pref.saveUser(userToken)
        }
    }
}