package com.vasberc.presentation.movie_detailed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vasberc.data.models.ErrorModel
import com.vasberc.data.models.Movie
import com.vasberc.data.models.MovieDetailed
import com.vasberc.data.models.MovieReview
import com.vasberc.data.models.SimilarMovie
import com.vasberc.data.repo.MoviesRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam

@KoinViewModel
class MovieDetailedViewModel(
    private val moviesRepo: MoviesRepo,
    @InjectedParam
    private val movieId: Int
): ViewModel() {

    private val _movieState: MutableStateFlow<MovieScreenState> = MutableStateFlow(MovieScreenState())
    val movieState: StateFlow<MovieScreenState> = _movieState.asStateFlow()

    private val _similarMovies: MutableStateFlow<List<SimilarMovie>?> = MutableStateFlow(null)
    val similarMovies: StateFlow<List<SimilarMovie>?> = _similarMovies.asStateFlow()

    private val _movieReviews: MutableStateFlow<List<MovieReview>?> = MutableStateFlow(null)
    val movieReviews: StateFlow<List<MovieReview>?> = _movieReviews.asStateFlow()

    init {

        if(_movieState.value.movie == null) {
            getMovie()
        }

        if(_movieReviews.value == null) {
            getReviews()
        }

        if(_similarMovies.value == null) {
            getSimilarMovies()
        }
    }

    private fun getSimilarMovies() {
        viewModelScope.launch {
            moviesRepo.getSimilarMoviesByMovieId(movieId).collect { resultState ->
                resultState.handle(
                    onLoading = {
                        _similarMovies.value = null
                    },
                    onSuccess = {
                        _similarMovies.value = it
                    },
                    onError = {
                        _similarMovies.value = listOf()
                    }
                )
            }
        }
    }

    private fun getReviews() {
        viewModelScope.launch {
            moviesRepo.getReviewsByMovieId(movieId).collect { resultState ->
                resultState.handle(
                    onLoading = {
                        _movieReviews.value = null
                    },
                    onSuccess = {
                        _movieReviews.value = it
                    },
                    onError = {
                        _movieReviews.value = listOf()
                    }
                )
            }
        }
    }

    private fun getMovie() {
        viewModelScope.launch {
            moviesRepo.getMovieDetailedByMovieId(movieId).collect { resultState ->
                resultState.handle(
                    onLoading = {
                        _movieState.value = _movieState.value.copy(isLoading = true)
                    },
                    onSuccess = {
                        _movieState.value = MovieScreenState(isLoading = false, movie = it)
                    },
                    onError = {
                        val message = if(it is ErrorModel.NetworkError) {
                            "Your connection arrears to be offline, please try again later"
                        } else {
                            "Something went wrong, please try again later"
                        }
                        _movieState.value = MovieScreenState(isLoading = false, movie = null, errorMessage = message)
                    }
                )
            }
        }
    }

    fun toggleFavourite() {
        viewModelScope.launch {
            val stateMovie = _movieState.value.movie
            stateMovie?.let {
                val isFavourite = moviesRepo.toggleFavourite(stateMovie.id)
                _movieState.value = _movieState.value.copy(movie = stateMovie.copy(isFavourite = isFavourite))
            }
        }
    }

    data class MovieScreenState(
        val isLoading: Boolean = false,
        val movie: MovieDetailed? = null,
        val errorMessage: String? = null
    )
}