package com.vicryfahreza.storyapp.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vicryfahreza.storyapp.data.paging.PreferenceStoryRepo
import com.vicryfahreza.storyapp.service.ApiConfig

class ViewModelFactory(private val pref: UserPreference): ViewModelProvider.NewInstanceFactory() {

    private val apiService = ApiConfig.getApiService()

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(pref) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(pref, PreferenceStoryRepo(apiService, pref)) as T
            }
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(pref) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(pref) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel Class : ${modelClass.name}")
        }
    }
}