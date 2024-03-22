package com.vasberc.data.repo

import com.vasberc.data.models.Movie
import com.vasberc.data.models.MovieDetailed
import com.vasberc.data.models.MovieReview
import com.vasberc.data.models.SimilarMovie
import com.vasberc.data.utils.ResultState
import kotlinx.coroutines.flow.Flow

interface MoviesRepo {
    suspend fun toggleFavourite(movieId: Int): Boolean

    fun getMovieDetailedByMovieId(movieId: Int): Flow<ResultState<MovieDetailed>>

    fun getReviewsByMovieId(movieId: Int): Flow<ResultState<List<MovieReview>>>

    fun getSimilarMoviesByMovieId(movieId: Int): Flow<ResultState<List<SimilarMovie>>>
}