package com.vasberc.presentation.homescreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vasberc.presentation.databinding.HeaderAndFooterViewItemBinding

class MediatorLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<MediatorLoadStateAdapter.MediatorLoadStateViewHolder>() {
    override fun onBindViewHolder(holder: MediatorLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): MediatorLoadStateViewHolder {
        val binding = HeaderAndFooterViewItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MediatorLoadStateViewHolder(binding)
    }

    inner class MediatorLoadStateViewHolder (
        private val binding: HeaderAndFooterViewItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.retryButton.also {
                it.setOnClickListener { retry() }
            }
        }

        fun bind(loadState: LoadState) {
            if(loadState is LoadState.Error) {
                binding.footerProgressBar.isVisible = false
            }
            binding.retryButton.isVisible = loadState !is LoadState.Loading
        }
    }
}