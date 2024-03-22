package com.vasberc.presentation.movie_detailed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.vasberc.data.models.SimilarMovie
import com.vasberc.presentation.R
import com.vasberc.presentation.databinding.SimilarMovieListItemBinding
import com.vasberc.presentation.utils.createShimmerDrawable
import com.vasberc.presentation.utils.loadFromNetWork
import com.vasberc.presentation.utils.setCornersRadius
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class SimilarMoviesAdapter: ListAdapter<SimilarMovie, SimilarMoviesAdapter.SimilarMovieViewHolder>(
    object : DiffUtil.ItemCallback<SimilarMovie>() {
        override fun areItemsTheSame(oldItem: SimilarMovie, newItem: SimilarMovie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SimilarMovie, newItem: SimilarMovie): Boolean {
            return oldItem == newItem
        }

    }
) {

    private val rootClicksChannel = Channel<SimilarMovie>(Channel.RENDEZVOUS)
    val rootClicks: Flow<SimilarMovie> = rootClicksChannel.receiveAsFlow()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilarMovieViewHolder {
        return SimilarMovieViewHolder(SimilarMovieListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: SimilarMovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SimilarMovieViewHolder(private val binding: SimilarMovieListItemBinding): RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setCornersRadius(true, true, true, true)
        }

        fun bind(item: SimilarMovie?) {
            if(item == null) {
                binding.root.load(createShimmerDrawable())
            } else {
                binding.root.loadFromNetWork(item.backdropPath, createShimmerDrawable(), R.drawable.ic_no_image_placeholder)
                binding.root.setOnClickListener {
                    rootClicksChannel.trySend(item)
                }
            }
        }
    }

}