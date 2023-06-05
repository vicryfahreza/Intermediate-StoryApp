package com.vicryfahreza.storyapp.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData


class AddStoryViewModel (private val pref: UserPreference) : ViewModel() {

    fun getUser(): LiveData<String> {
        return pref.getUser().asLiveData()
    }

}


