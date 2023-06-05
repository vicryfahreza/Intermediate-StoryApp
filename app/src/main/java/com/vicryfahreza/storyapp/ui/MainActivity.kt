package com.vicryfahreza.storyapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.vicryfahreza.storyapp.R
import com.vicryfahreza.storyapp.adapter.LoadingPagingAdapter
import com.vicryfahreza.storyapp.adapter.StoriesAdapter
import com.vicryfahreza.storyapp.databinding.ActivityMainBinding
import com.vicryfahreza.storyapp.model.MainViewModel
import com.vicryfahreza.storyapp.model.UserPreference
import com.vicryfahreza.storyapp.model.ViewModelFactory

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var adapter: StoriesAdapter

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.logout_page -> {
                mainViewModel.saveUser("")
                val intentLogout = Intent(this, LoginActivity::class.java)
                val intentFlag = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                intentLogout.flags = intentFlag
                startActivity(intentLogout)
                true
            }
            R.id.add_page -> {
                val intentAddStory = Intent(this, AddStoryActivity::class.java)
                startActivity(intentAddStory)
                true
            }
            R.id.map_page -> {
                val intentMapStory = Intent(this, MapsActivity::class.java)
                startActivity(intentMapStory)
                true
            }
            else -> true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        val pref = UserPreference.getInstance(dataStore)
        mainViewModel = ViewModelProvider(this, ViewModelFactory(pref))[MainViewModel::class.java]



        val layoutManager = LinearLayoutManager(this)
        mainBinding.rvStory.layoutManager = layoutManager

        runRVMain()
        getRVPaging()
    }

    private fun runRVMain(){
        mainBinding.apply {

            val rvStory = ObjectAnimator.ofFloat(rvStory, View.ALPHA, 1f).setDuration(500)

            AnimatorSet().apply {
                playSequentially(rvStory)
                start()
            }
        }
    }

    private fun getRVPaging() {
        mainViewModel.getUser().observe(this) { token ->
            accessMainActivity(token)
        }
    }

    private fun accessMainActivity(token: String) {
        if (token.isEmpty()) {
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
            finish()
        } else {
            getData()
        }
    }

    private fun getData() {
        adapter = StoriesAdapter()
        mainBinding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingPagingAdapter {
                adapter.retry()
            }
        )
        mainViewModel.storyPaging.observe(this@MainActivity ){ paging ->
            adapter.submitData(lifecycle, paging)
        }
    }

}