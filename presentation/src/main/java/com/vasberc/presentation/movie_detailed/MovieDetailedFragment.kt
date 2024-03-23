package com.vasberc.presentation.movie_detailed

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import com.vasberc.data.models.MovieDetailed
import com.vasberc.presentation.R
import com.vasberc.presentation.databinding.MovieDetailViewFragmentBinding
import com.vasberc.presentation.utils.BaseFragment
import com.vasberc.presentation.utils.createShimmerDrawable
import com.vasberc.presentation.utils.loadFromNetWork
import com.vasberc.presentation.utils.setCornersRadius
import com.vasberc.presentation.utils.snack
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class MovieDetailedFragment: BaseFragment<MovieDetailViewFragmentBinding>(R.layout.movie_detail_view_fragment)  {

    override val homeAsUp: Boolean = true
    override val supportActionBar: Int = R.id.tb_movie_detail_view
    override val title: String by lazy {
        arguments?.getString("title", "") ?: ""
    }

    private val similarMoviesAdapter: SimilarMoviesAdapter? get() {
        return try {
            binding.rvSimilar.adapter as SimilarMoviesAdapter
        } catch (e: Exception) {
            null
        }
    }

    private val reviewsAdapter: MovieReviewsAdapter? get() {
        return try {
            binding.rvReviews.adapter as MovieReviewsAdapter
        } catch (e: Exception) {
            null
        }
    }

    private val viewModel: MovieDetailedViewModel by viewModel(parameters = { parametersOf(arguments?.getInt("movieId", -1) ?: -1) })

    override fun createViewBinding(view: View): MovieDetailViewFragmentBinding {
        return MovieDetailViewFragmentBinding.bind(view)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeView()
        setUpListeners()
        setUpFlowCollectors()
    }

    private fun initializeView() {
        binding.ivMovieDetailCover.setCornersRadius(topLeft = true, topRight = true)

        if(reviewsAdapter == null) {
            binding.rvReviews.adapter = MovieReviewsAdapter()
            binding.rvReviews.layoutManager = LinearLayoutManager(requireContext(), HORIZONTAL, false)
        }

        if(similarMoviesAdapter == null) {
            binding.rvSimilar.adapter = SimilarMoviesAdapter()
            binding.rvSimilar.layoutManager = LinearLayoutManager(requireContext(), HORIZONTAL, false)
        }
    }

    private fun setUpListeners() {
        binding.ivMovieDetailFavouriteState.setOnClickListener {
            viewModel.toggleFavourite()
        }

        binding.ivShare.setOnClickListener {
            viewModel.movieState.value.movie?.let {
                val textToShare = "Hey watch this out, ${it.title} it is the best movie.\n ${it.homepage}"
                val intent = Intent(Intent.ACTION_SEND)

                intent.setType("text/plain")
                intent.putExtra(Intent.EXTRA_TEXT, textToShare)

                startActivity(Intent.createChooser(intent, "Share"))
            }

        }

        reviewsAdapter?.readMoreClicks?.onEach {
            navigate(MovieDetailedFragmentDirections.actionMovieDetailedFragmentToWebViewFragment(it))
        }?.launchIn(viewLifecycleOwner.lifecycleScope)

        similarMoviesAdapter?.rootClicks?.onEach {
            navigate(MovieDetailedFragmentDirections.actionMovieDetailedFragmentSelf(it.id, it.title))
        }?.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun setUpFlowCollectors() {
        viewModel.movieState.onEach {
            if(it.isLoading && it.movie == null) {
                onLoadingState()
            } else if(it.movie != null) {
                populateFields(it.movie)
            } else if(it.errorMessage != null) {
                handleError(it.errorMessage)
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.movieReviews.onEach { reviews ->
            if(reviews == null) {
                //loading state
                reviewsAdapter?.submitList(listOf(null, null, null))
                binding.rvReviews.isVisible = true
                binding.tvReviewsTitle.isVisible = true
            } else if (reviews.isNotEmpty()) {
                //success
                reviewsAdapter?.submitList(reviews)
                binding.rvReviews.isVisible = true
                binding.tvReviewsTitle.isVisible = true
            } else {
                //error or not found
                binding.rvReviews.isVisible = false
                binding.tvReviewsTitle.isVisible = false
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.similarMovies.onEach { similarMovies ->
            if(similarMovies == null) {
                //loading state
                similarMoviesAdapter?.submitList(listOf(null, null, null, null, null, null))
                binding.rvSimilar.isVisible = true
                binding.tvSimilarTitle.isVisible = true
            } else if (similarMovies.isNotEmpty()) {
                //success
                similarMoviesAdapter?.submitList(similarMovies)
                binding.rvSimilar.isVisible = true
                binding.tvSimilarTitle.isVisible = true
            } else {
                //error or not found
                binding.rvSimilar.isVisible = false
                binding.tvSimilarTitle.isVisible = false
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    @SuppressLint("SetTextI18n")
    private fun populateFields(movie: MovieDetailed) {
        binding.includedLoadingLayout.root.isVisible = false
        binding.svMainContent.isVisible = true
        binding.includedLoadingLayout.root.stopShimmer()
        binding.ivMovieDetailCover.loadFromNetWork(
            movie.backdropPath,
            createShimmerDrawable(),
            R.drawable.ic_no_image_placeholder
        )
        binding.ivShare.isVisible = movie.homepage.isNotEmpty()
        binding.tvMovieDetailTitle.text = movie.title
        binding.tvMovieDetailGenres.text = movie.genres.asSequence().map { it.name }.joinToString(", ")
        binding.tvMovieDetailReleaseDate.text = movie.releaseDate
        binding.rbMovieDetailAverageRating.rating = movie.voteAverage.toFloat() / 2
        binding.tvMovieRuntime.text = "${movie.runtime / 60}h ${movie.runtime % 60}m"
        binding.ivMovieDetailFavouriteState.setImageResource(
            if(movie.isFavourite) R.drawable.baseline_favorite_24 else R.drawable.outline_favorite_border_24_black
        )
        binding.tvMovieDescription.text = movie.overview
        binding.tvMovieCast.text = movie.cast.asSequence().map { it.name }.joinToString(", ")
    }

    private fun handleError(errorMessage: String) {
        binding.root.snack(errorMessage)
        activity?.onBackPressed()
    }

    private fun onLoadingState() {
        binding.includedLoadingLayout.root.isVisible = true
        binding.svMainContent.isVisible = false
        binding.includedLoadingLayout.root.startShimmer()
    }
}