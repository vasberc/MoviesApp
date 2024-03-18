package com.vasberc.presentation.homescreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.vasberc.data.models.Movie
import com.vasberc.presentation.R
import com.vasberc.presentation.databinding.PopularMovieItemBinding

class PopularMoviesPagingAdapter: PagingDataAdapter<Movie, PopularMoviesPagingAdapter.PopularMoviesViewHolder>(
    object : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }

    }
) {
    override fun onBindViewHolder(holder: PopularMoviesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularMoviesViewHolder {
        val binding = PopularMovieItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PopularMoviesViewHolder(binding)
    }

    inner class PopularMoviesViewHolder(private val binding: PopularMovieItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Movie?) {
            binding.sflLoading.isVisible = item == null
            binding.clItem.isVisible =  !binding.sflLoading.isVisible

            if(item != null) {
                binding.ivMovieImage.load(item.backdropPath)
                binding.tvMovieTitle.text = item.title
                binding.rbMovieAverageRating.rating = item.voteAverage.toFloat() / 2
                binding.ivMovieFavouriteState.load(
                    ContextCompat.getDrawable(
                        binding.root.context,
                        if(item.isFavourite) R.drawable.baseline_favorite_24 else R.drawable.outline_favorite_border_24
                    )
                )
                binding.tvMovieReleaseDate.text = item.releaseDate
            }
        }
    }
}