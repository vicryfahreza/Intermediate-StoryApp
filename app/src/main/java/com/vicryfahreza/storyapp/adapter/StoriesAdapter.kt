package com.vicryfahreza.storyapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vicryfahreza.storyapp.databinding.RowItemBinding
import com.vicryfahreza.storyapp.response.ListStoryItem
import com.vicryfahreza.storyapp.ui.DetailActivity

class StoriesAdapter
    : PagingDataAdapter<ListStoryItem, StoriesAdapter.ViewHolder>(DIFF_CALLBACK) {

    class ViewHolder(private val binding: RowItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(story: ListStoryItem) {
            binding.apply {
                tvName.text = story.name
                tvDescription.text = story.description
                Glide.with(itemView.context).load(story.photoUrl).into(ivPhotoStory)

                itemView.setOnClickListener {
                    val detailStoryUserIntent = Intent(itemView.context, DetailActivity::class.java)
                    detailStoryUserIntent.putExtra(DetailActivity.EXTRA_PHOTO_URL, story.photoUrl)
                    detailStoryUserIntent.putExtra(DetailActivity.EXTRA_USERNAME, story.name)
                    detailStoryUserIntent.putExtra(DetailActivity.EXTRA_DESCRIPTION, story.description)
                    itemView.context.startActivity(detailStoryUserIntent)
                }


            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RowItemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false))
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null){
            viewHolder.bind(data)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}