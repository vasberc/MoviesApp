package com.vasberc.data_remote.service

import com.vasberc.data_remote.response_model.ConfigurationResponse
import com.vasberc.data_remote.response_model.GetMovieResponse
import com.vasberc.data_remote.response_model.GetMoviesReviewsResponse
import com.vasberc.data_remote.response_model.GetPopularMoviesResponse
import com.vasberc.data_remote.response_model.GetSimilarMoviesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MoviesService {

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("language")
        language: String,
        @Query("page")
        page: Int
    ): Response<GetPopularMoviesResponse>

    @GET("movie/{movie_id}")
    suspend fun getMovie(
        @Query("language")
        language: String,
        @Path("movie_id")
        movieId: Int,
        @Query("append_to_response")
        appendToResponse: String = "credits"
    ): Response<GetMovieResponse>

    @GET("movie/{movie_id}/reviews")
    suspend fun getMoviesReview(
        @Query("language")
        language: String,
        @Path("movie_id")
        movieId: Int,
        @Query("page")
        page: Int
    ): Response<GetMoviesReviewsResponse>

    @GET("movie/{movie_id}/similar")
    suspend fun getSimilarMovies(
        @Query("language")
        language: String,
        @Path("movie_id")
        movieId: Int,
        @Query("page")
        page: Int
    ): Response<GetSimilarMoviesResponse>

    @GET("configuration")
    suspend fun getConfiguration(): Response<ConfigurationResponse>

}