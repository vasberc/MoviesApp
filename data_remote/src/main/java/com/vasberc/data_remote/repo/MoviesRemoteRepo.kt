package com.vasberc.data_remote.repo

import com.haroldadmin.cnradapter.NetworkResponse
import com.vasberc.data_remote.response_model.ConfigurationResponse
import com.vasberc.data_remote.response_model.ErrorResponseModel
import com.vasberc.data_remote.response_model.GetMovieResponse
import com.vasberc.data_remote.response_model.GetMoviesReviewsResponse
import com.vasberc.data_remote.response_model.GetSimilarMoviesResponse

interface MoviesRemoteRepo {
    suspend fun getMovieById(movieId: Int): NetworkResponse<GetMovieResponse, ErrorResponseModel>

    suspend fun getReviewsByMovieId(movieId: Int): NetworkResponse<GetMoviesReviewsResponse, ErrorResponseModel>

    suspend fun getSimilarMoviesByMovieId(movieId: Int): NetworkResponse<GetSimilarMoviesResponse, ErrorResponseModel>

    suspend fun getConfiguration(): NetworkResponse<ConfigurationResponse, ErrorResponseModel>
}