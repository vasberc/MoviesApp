package com.vasberc.data.models

import com.vasberc.data_remote.response_model.GetMovieResponse

data class MovieDetailed(
    val backdropPath: String,
    val credits: Credits?,
    val genres: List<Genre>,
    val homepage: String?,
    val id: Int,
    val imdbId: String?,
    val originalTitle: String,
    val posterPath: String,
    val releaseDate: String,
    val runtime: Int,
    val title: String,
    val voteAverage: Double,
) {
    data class Credits(
        val cast: List<Cast>,
        val crew: List<Crew>
    ) {
        data class Cast(
            val id: Int,
            val name: String,
            val originalName: String
        )

        data class Crew(
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

fun GetMovieResponse.asMovieDetailed(imageBaseUrl: String, imageSize: String): MovieDetailed {
    return MovieDetailed(
        backdropPath = backdropPath?.let { "$imageBaseUrl$imageSize/$it" } ?: "",
        credits = credits?.asMovieDetailedCredits(),
        genres = genres?.mapNotNull { it?.asMovieDetailedGenre() } ?: listOf(),
        homepage = homepage ?: "",
        id = id ?: -1,
        imdbId = imdbId ?: "",
        originalTitle = originalTitle ?: "",
        posterPath = posterPath ?: "",
        releaseDate = releaseDate ?: "",
        runtime = runtime ?: -1,
        title = title ?: "",
        voteAverage = voteAverage ?: -0.0
    )
}

fun GetMovieResponse.Credits?.asMovieDetailedCredits(): MovieDetailed.Credits {
    return MovieDetailed.Credits(
        cast = this?.cast?.mapNotNull { it?.asMovieDetailedCast() } ?: listOf(),
        crew = this?.crew?.mapNotNull { it?.asMovieDetailedCrew() } ?: listOf()
    )
}

fun GetMovieResponse.Credits.Cast.asMovieDetailedCast() : MovieDetailed.Credits.Cast {
    return MovieDetailed.Credits.Cast(
        id = id ?: -1,
        name = name ?: "",
        originalName = originalName ?: ""
    )
}
fun GetMovieResponse.Credits.Crew.asMovieDetailedCrew() : MovieDetailed.Credits.Crew {
    return MovieDetailed.Credits.Crew(
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