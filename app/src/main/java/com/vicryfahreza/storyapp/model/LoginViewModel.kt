package com.vicryfahreza.storyapp.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LoginViewModel(private val pref: UserPreference): ViewModel() {

    fun saveUser(user: String) {
        viewModelScope.launch {
            pref.saveUser(user)
        }
    }


}