package com.vasberc.data.models

import com.vasberc.data_remote.response_model.GetMovieResponse

data class MovieDetailed(
    val backdropPath: String,
    val credits: Credits?,
    val genres: List<Genre>,
    val homepage: String,
    val id: Int,
    val releaseDate: String,
    val runtime: Int,
    val title: String,
    val voteAverage: Double,
    val isFavourite: Boolean,
    val overview: String
) {
    data class Credits(
        val cast: List<Cast>
    ) {
        data class Cast(
            val id: Int,
            val name: String,
            val originalName: String
        )
    }

    data class Genre(
        val id: Int?,
        val name: String?
    )
}

fun GetMovieResponse.asMovieDetailed(imageBaseUrl: String, imageSize: String, isFavourite: Boolean): MovieDetailed {
    return MovieDetailed(
        backdropPath = backdropPath?.let { "$imageBaseUrl$imageSize/$it" } ?: "",
        credits = credits?.asMovieDetailedCredits(),
        genres = genres?.mapNotNull { it?.asMovieDetailedGenre() } ?: listOf(),
        homepage = homepage ?: "",
        id = id ?: -1,
        releaseDate = releaseDate ?: "",
        runtime = runtime ?: -1,
        title = title ?: "",
        voteAverage = voteAverage ?: -0.0,
        isFavourite = isFavourite,
        overview = overview ?: ""

    )
}

fun GetMovieResponse.Credits?.asMovieDetailedCredits(): MovieDetailed.Credits {
    return MovieDetailed.Credits(
        cast = this?.cast?.mapNotNull { it?.asMovieDetailedCast() } ?: listOf()
    )
}

fun GetMovieResponse.Credits.Cast.asMovieDetailedCast() : MovieDetailed.Credits.Cast {
    return MovieDetailed.Credits.Cast(
        id = id ?: -1,
        name = name ?: "",
        originalName = originalName ?: ""
    )
}

fun GetMovieResponse.Genre?.asMovieDetailedGenre(): MovieDetailed.Genre {
    return MovieDetailed.Genre(
        id = this?.id ?: -1,
        name = this?.name ?: ""
    )
}