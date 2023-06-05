package com.vicryfahreza.storyapp.model

import android.util.Log
import androidx.lifecycle.*
import com.vicryfahreza.storyapp.response.AllStoryResponse
import com.vicryfahreza.storyapp.response.ListStoryItem
import com.vicryfahreza.storyapp.service.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel(private val pref: UserPreference): ViewModel() {
    private val _markerStory = MutableLiveData<ArrayList<ListStoryItem>>()
    val markerStory: LiveData<ArrayList<ListStoryItem>> = _markerStory

    fun getUser(): LiveData<String> {
        return pref.getUser().asLiveData()
    }

    fun markerStory(userToken: String){
        val client = ApiConfig.getApiService().getTokenForMap("Bearer $userToken")
        client.enqueue(object : Callback<AllStoryResponse> {
            override fun onResponse(
                call: Call<AllStoryResponse>,
                response: Response<AllStoryResponse>
            ) {
                if(response.isSuccessful){
                    _markerStory.value = response.body()?.listStory as ArrayList<ListStoryItem>
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<AllStoryResponse>, t: Throwable) {
                Log.e(TAG, "onFailure : ${t.message.toString()}")

            }

        })
    }

    companion object {
        private const val TAG = "MainViewModel"
    }

}