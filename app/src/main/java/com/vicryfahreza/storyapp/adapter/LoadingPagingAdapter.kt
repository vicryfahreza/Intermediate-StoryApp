package com.vicryfahreza.storyapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vicryfahreza.storyapp.databinding.LoadingStoryBinding

class LoadingPagingAdapter(private val retry: () -> Unit) : LoadStateAdapter<LoadingPagingAdapter.LoadingViewHolder>() {
    class LoadingViewHolder(private val loadingBinding: LoadingStoryBinding, retry: () -> Unit) :
        RecyclerView.ViewHolder(loadingBinding.root) {
        init {
            loadingBinding.retryButton.setOnClickListener { retry.invoke() }
        }
        fun bind(loadState: LoadState){
            if(loadState is LoadState.Error){
                loadingBinding.errorMsg.text = loadState.error.localizedMessage
            }
            loadingBinding.progressBar.isVisible = loadState is LoadState.Loading
            loadingBinding.retryButton.isVisible = loadState is LoadState.Error
            loadingBinding.errorMsg.isVisible = loadState is LoadState.Error
        }
    }

    override fun onBindViewHolder(holder: LoadingViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState
    ): LoadingViewHolder {
        val binding = LoadingStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadingViewHolder(binding, retry)
    }
}