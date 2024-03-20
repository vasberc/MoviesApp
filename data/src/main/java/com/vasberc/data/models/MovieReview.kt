package com.vasberc.data.models

import com.vasberc.data_remote.response_model.GetMoviesReviewsResponse

data class MovieReview(
    val author: String,
    val authorDetails: AuthorDetails,
    val content: String,
    val createdAt: String,
    val id: String,
    val updatedAt: String,
    val url: String
) {
    data class AuthorDetails(
        val avatarPath: String,
        val rating: Double,
    )
}

fun GetMoviesReviewsResponse.asMovieReviews(imageBaseUrl: String, imageSize: String): List<MovieReview> {
    return this.results?.mapNotNull { it?.asMovieReview(imageBaseUrl, imageSize) } ?: listOf()
}

fun GetMoviesReviewsResponse.Result.asMovieReview(imageBaseUrl: String, imageSize: String): MovieReview {
    return MovieReview(
        author = author ?: "",
        authorDetails = authorDetails.asAuthorDetails(imageBaseUrl, imageSize),
        content = content ?: "",
        createdAt = createdAt ?: "",
        id = id ?: "",
        updatedAt = updatedAt ?: "",
        url = url ?: ""
    )
}

fun GetMoviesReviewsResponse.Result.AuthorDetails?.asAuthorDetails(imageBaseUrl: String, imageSize: String): MovieReview.AuthorDetails {
    return MovieReview.AuthorDetails(
        avatarPath = this?.avatarPath?.toString()?.let { "$imageBaseUrl$imageSize/$it" } ?: "",
        rating = this?.rating ?: 0.0
    )
}