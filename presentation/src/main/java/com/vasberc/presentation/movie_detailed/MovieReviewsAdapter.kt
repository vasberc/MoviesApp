package com.vasberc.presentation.movie_detailed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vasberc.data.models.Movie
import com.vasberc.data.models.MovieReview
import com.vasberc.data.utils.DATE_NO_TIME_US_PATTERN
import com.vasberc.data.utils.localDateTimeToStringWithDesiredPattern
import com.vasberc.presentation.R
import com.vasberc.presentation.databinding.ReviewListItemBinding
import com.vasberc.presentation.utils.createShimmerDrawable
import com.vasberc.presentation.utils.loadFromNetWork
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import java.time.ZonedDateTime

class MovieReviewsAdapter: ListAdapter<MovieReview, MovieReviewsAdapter.ReviewHolder>(
    object: DiffUtil.ItemCallback<MovieReview>() {
        override fun areItemsTheSame(oldItem: MovieReview, newItem: MovieReview): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MovieReview, newItem: MovieReview): Boolean {
            return oldItem == newItem
        }

    }
) {
    private val readMoreClicksChannel = Channel<String>(Channel.RENDEZVOUS)
    val readMoreClicks: Flow<String> = readMoreClicksChannel.receiveAsFlow()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewHolder {
        return ReviewHolder(ReviewListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ReviewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ReviewHolder(private val binding: ReviewListItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MovieReview?) {
            if(item == null) {
                binding.includedLoadingLayout.root.isVisible = true
                binding.clMainContent.isVisible = false
            } else {
                binding.includedLoadingLayout.root.isVisible = false
                binding.clMainContent.isVisible = true
                binding.ivReviewerAvatar.loadFromNetWork(
                    item.authorDetails.avatarPath,
                    createShimmerDrawable(),
                    R.drawable.ic_user_icon
                )
                binding.rbMovieRating.rating = item.authorDetails.rating.toFloat() / 2
                val date = ZonedDateTime.parse(item.updatedAt).toLocalDateTime()
                binding.tvDateUpdated.text = localDateTimeToStringWithDesiredPattern(date, DATE_NO_TIME_US_PATTERN)
                binding.tvContent.text = item.content
                binding.tvAuthorName.text = item.author

                binding.tvReadMore.setOnClickListener {
                    readMoreClicksChannel.trySend(item.url)
                }
            }
        }
    }
}