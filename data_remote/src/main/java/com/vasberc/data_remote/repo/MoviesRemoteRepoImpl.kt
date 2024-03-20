package com.vasberc.data_remote.repo

import com.haroldadmin.cnradapter.NetworkResponse
import com.vasberc.data_remote.response_model.ConfigurationResponse
import com.vasberc.data_remote.response_model.ErrorResponseModel
import com.vasberc.data_remote.response_model.GetMovieResponse
import com.vasberc.data_remote.response_model.GetMoviesReviewsResponse
import com.vasberc.data_remote.response_model.GetSimilarMoviesResponse
import com.vasberc.data_remote.service.MoviesService
import org.koin.core.annotation.Single

@Single
class MoviesRemoteRepoImpl(private val service: MoviesService):
    MoviesRemoteRepo {
    override suspend fun getMovieById(movieId: Int): NetworkResponse<GetMovieResponse, ErrorResponseModel> {
        return service.getMovie("en-US", movieId)
    }

    override suspend fun getReviewsByMovieId(movieId: Int): NetworkResponse<GetMoviesReviewsResponse, ErrorResponseModel> {
        return service.getMoviesReview("en-US", movieId, 1)
    }

    override suspend fun getSimilarMoviesByMovieId(movieId: Int): NetworkResponse<GetSimilarMoviesResponse, ErrorResponseModel> {
        return service.getSimilarMovies("en-US", movieId, 1)
    }

    override suspend fun getConfiguration(): NetworkResponse<ConfigurationResponse, ErrorResponseModel> {
        return service.getConfiguration()
    }


}