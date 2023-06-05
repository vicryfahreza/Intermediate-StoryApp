package com.vicryfahreza.storyapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.vicryfahreza.storyapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = intent.getStringExtra(EXTRA_USERNAME)
        val description = intent.getStringExtra(EXTRA_DESCRIPTION)
        val photoUrl = intent.getStringExtra(EXTRA_PHOTO_URL)

        binding.apply {
            Glide
                .with(this@DetailActivity)
                .load(photoUrl)
                .circleCrop()
                .centerCrop()
                .into(tvImgStory)
            tvUsernameStory.text = username
            tvDescriptionStory.text = description
        }
        runDetailAnimation()
    }

    private fun runDetailAnimation() {
        binding.apply {
            val tvImageStory = ObjectAnimator.ofFloat(tvImgStory, View.ALPHA, 1f).setDuration(ALPHA_DURATION.toLong())
            val tvUsernameStory = ObjectAnimator.ofFloat(tvUsernameStory, View.ALPHA, 1f).setDuration(ALPHA_DURATION.toLong())
            val tvDescriptionLabel = ObjectAnimator.ofFloat(tvDescriptionLabel, View.ALPHA, 1f).setDuration(ALPHA_DURATION.toLong())
            val tvDescriptionStory = ObjectAnimator.ofFloat(tvDescriptionStory, View.ALPHA, 1f).setDuration(ALPHA_DURATION.toLong())

            AnimatorSet().apply {
                playSequentially(tvImageStory, tvUsernameStory, tvDescriptionLabel, tvDescriptionStory)
                start()
            }
        }
    }

    companion object {
        const val EXTRA_USERNAME = "extra_username"
        const val EXTRA_PHOTO_URL = "extra_photo_url"
        const val EXTRA_DESCRIPTION = "extra_description"
        const val ALPHA_DURATION = 500
    }
}