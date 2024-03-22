package com.vasberc.data.repo

import com.vasberc.data.configuration.Configuration
import com.vasberc.data.models.Movie
import com.vasberc.data.models.MovieDetailed
import com.vasberc.data.models.MovieReview
import com.vasberc.data.models.SimilarMovie
import com.vasberc.data.models.asErrorModel
import com.vasberc.data.models.asImagesConfiguration
import com.vasberc.data.models.asMovieDetailed
import com.vasberc.data.models.asMovieReviews
import com.vasberc.data.models.asSimilarMovies
import com.vasberc.data.utils.ResultState
import com.vasberc.data.utils.parseResponse
import com.vasberc.data_local.repo.MoviesLocalRepo
import com.vasberc.data_remote.repo.MoviesRemoteRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single

@Single
class MoviesRepoImpl(private val moviesLocalRepo: MoviesLocalRepo, private val moviesRemoteRepo: MoviesRemoteRepo): MoviesRepo {
    override suspend fun toggleFavourite(movieId: Int): Boolean {
       val result = withContext(Dispatchers.IO) {
            moviesLocalRepo.toggleFavourite(movieId)
           moviesLocalRepo.checkFavouriteById(movieId)
        }
        return result
    }

    override fun getMovieDetailedByMovieId(movieId: Int): Flow<ResultState<MovieDetailed>> {
        return flow {
            emit(ResultState.Loading)
            handleImagesConfiguration()
            val isFavourite = moviesLocalRepo.checkFavouriteById(movieId)
            val response = moviesRemoteRepo.getMovieById(movieId).parseResponse(
                successMapper = { asMovieDetailed(
                    imageBaseUrl = Configuration.images?.secureBaseUrl ?: "",
                    imageSize = Configuration.images?.backdropSizes?.get(2) ?: "",
                    isFavourite = isFavourite
                ) },
                serverErrorMapper = { responseCode ->
                    asErrorModel(responseCode)
                }
            )
            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    override fun getReviewsByMovieId(movieId: Int): Flow<ResultState<List<MovieReview>>> {
        return flow {
            emit(ResultState.Loading)
            handleImagesConfiguration()
            val response = moviesRemoteRepo.getReviewsByMovieId(movieId).parseResponse(
                successMapper = {
                    asMovieReviews(
                        imageBaseUrl = Configuration.images?.secureBaseUrl ?: "",
                        imageSize = Configuration.images?.profileSizes?.get(0) ?: ""
                    )
                },
                serverErrorMapper = {
                    asErrorModel(it)
                }
            )
            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    override fun getSimilarMoviesByMovieId(movieId: Int): Flow<ResultState<List<SimilarMovie>>> {
        return flow {
            emit(ResultState.Loading)
            handleImagesConfiguration()
            val response = moviesRemoteRepo.getSimilarMoviesByMovieId(movieId).parseResponse(
                successMapper = {
                    asSimilarMovies(
                        imageBaseUrl = Configuration.images?.secureBaseUrl ?: "",
                        imageSize = Configuration.images?.profileSizes?.get(2) ?: ""
                    )
                },
                serverErrorMapper = {
                    asErrorModel(it)
                }
            )
            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    private suspend fun handleImagesConfiguration() {
        if(Configuration.images == null) {
            val imagesConfigurationResponse = moviesRemoteRepo.getConfiguration().parseResponse(
                successMapper = { asImagesConfiguration() },
                serverErrorMapper = { errorCode ->
                    asErrorModel(errorCode)
                }
            )
            if(imagesConfigurationResponse is ResultState.Success) {
                Configuration.images = imagesConfigurationResponse.data
            }
        }
    }

}