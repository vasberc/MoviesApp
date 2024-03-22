package com.vasberc.data.models

import com.vasberc.data_remote.response_model.GetSimilarMoviesResponse

data class SimilarMovie(
    val id: Int,
    val backdropPath: String,
    val title: String
)

fun GetSimilarMoviesResponse.asSimilarMovies(imageBaseUrl: String, imageSize: String): List<SimilarMovie> {
    return results?.mapNotNull { it?.asSimilarMovie(imageBaseUrl, imageSize) } ?: listOf()
}

fun GetSimilarMoviesResponse.Result.asSimilarMovie(imageBaseUrl: String, imageSize: String): SimilarMovie {
    return SimilarMovie(
        id = id ?: -1,
        backdropPath = backdropPath?.let { "$imageBaseUrl$imageSize/$it" } ?: "",
        title = title ?: ""
    )
}