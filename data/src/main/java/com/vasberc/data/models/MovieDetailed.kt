package com.vasberc.data.models

import com.vasberc.data_local.entities.CastEntity
import com.vasberc.data_local.entities.GenreEntity
import com.vasberc.data_local.entities.MovieCastEntity
import com.vasberc.data_local.entities.MovieCastEntityWithRelations
import com.vasberc.data_local.entities.MovieDetailedEntity
import com.vasberc.data_local.entities.MovieDetailedEntityWithRelations
import com.vasberc.data_local.entities.MovieGenreEntity
import com.vasberc.data_local.entities.MovieGenreEntityWithRelations
import com.vasberc.data_remote.response_model.GetMovieResponse

data class MovieDetailed(
    val backdropPath: String,
    val cast: List<Cast>,
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
    fun asMovieDetailedEntityWithRelations(): MovieDetailedEntityWithRelations {
        return MovieDetailedEntityWithRelations(
            movieDetailedEntity = MovieDetailedEntity(
                backdropPath = backdropPath,
                homepage = homepage,
                id = id,
                releaseDate = releaseDate,
                runtime = runtime,
                title = title,
                voteAverage = voteAverage,
                isFavourite = isFavourite,
                overview = overview
            ),
            movieCast = cast.map { it.asMovieCastWithRelations(movieId = id) },
            movieGenres = genres.map { it.asMovieGenreWithRelations(movieId = id) }
        )
    }
    data class Cast(
        val id: Int,
        val name: String
    ) {
        fun asMovieCastWithRelations(movieId: Int): MovieCastEntityWithRelations {
            return MovieCastEntityWithRelations(
                movieCastEntity = MovieCastEntity(movieId = movieId, castId = id),
                cast = CastEntity(id = id, name = name)
            )
        }
    }

    data class Genre(
        val id: Int,
        val name: String
    ) {
        fun asMovieGenreWithRelations(movieId: Int): MovieGenreEntityWithRelations {
            return MovieGenreEntityWithRelations(
                movieGenreEntity = MovieGenreEntity(movieId = movieId, genreId = id),
                genre = GenreEntity(id = id, name = name)
            )
        }
    }
}

fun MovieDetailedEntityWithRelations.asMovieDetailed(): MovieDetailed {
    return MovieDetailed(
        backdropPath = movieDetailedEntity.backdropPath,
        cast = movieCast.map { it.asMovieDetailedCast() },
        genres = movieGenres.map { it.asMovieDetailedGenre() },
        homepage = movieDetailedEntity.homepage,
        id = movieDetailedEntity.id,
        releaseDate = movieDetailedEntity.releaseDate,
        runtime = movieDetailedEntity.runtime,
        title = movieDetailedEntity.title,
        voteAverage = movieDetailedEntity.voteAverage,
        isFavourite = movieDetailedEntity.isFavourite,
        overview = movieDetailedEntity.overview
    )
}

fun MovieCastEntityWithRelations.asMovieDetailedCast(): MovieDetailed.Cast {
    return MovieDetailed.Cast(
        id = cast.id,
        name = cast.name
    )
}

fun MovieGenreEntityWithRelations.asMovieDetailedGenre(): MovieDetailed.Genre {
    return MovieDetailed.Genre(
        id = genre.id,
        name = genre.name
    )
}

fun GetMovieResponse.asMovieDetailed(imageBaseUrl: String, imageSize: String, isFavourite: Boolean): MovieDetailed {
    return MovieDetailed(
        backdropPath = backdropPath?.let { "$imageBaseUrl$imageSize/$it" } ?: "",
        cast = credits?.cast?.mapNotNull { it?.asMovieDetailedCast() } ?: listOf(),
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

fun GetMovieResponse.Credits.Cast.asMovieDetailedCast() : MovieDetailed.Cast {
    return MovieDetailed.Cast(
        id = id ?: -1,
        name = name ?: ""
    )
}

fun GetMovieResponse.Genre.asMovieDetailedGenre(): MovieDetailed.Genre {
    return MovieDetailed.Genre(
        id = id ?: -1,
        name = name ?: ""
    )
}