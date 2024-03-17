package com.vasberc.data_remote.service

import com.haroldadmin.cnradapter.NetworkResponse
import com.vasberc.data_remote.response_model.ConfigurationResponse
import com.vasberc.data_remote.response_model.ErrorResponseModel
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
    ): GetPopularMoviesResponse

    @GET("movie/{movie_id}")
    suspend fun getMovie(
        @Query("language")
        language: String,
        @Path("movie_id")
        movieId: Int,
        @Query("append_to_response")
        appendToResponse: String = "credits"
    ): NetworkResponse<GetMovieResponse, ErrorResponseModel>

    @GET("movie/{movie_id}/reviews")
    suspend fun getMoviesReview(
        @Query("language")
        language: String,
        @Path("movie_id")
        movieId: Int,
        @Query("page")
        page: Int
    ): NetworkResponse<GetMoviesReviewsResponse, ErrorResponseModel>

    @GET("movie/{movie_id}/similar")
    suspend fun getSimilarMovies(
        @Query("language")
        language: String,
        @Path("movie_id")
        movieId: Int,
        @Query("page")
        page: Int
    ): NetworkResponse<GetSimilarMoviesResponse, ErrorResponseModel>

    @GET("configuration")
    suspend fun getConfiguration(): NetworkResponse<ConfigurationResponse, ErrorResponseModel>

}