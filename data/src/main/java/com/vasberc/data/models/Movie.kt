package com.vasberc.data.models

import com.vasberc.data_local.entities.CachedMovieEntity
import com.vasberc.data_local.entities.FavouriteEntity
import com.vasberc.data_local.entities.MovieAndFavoriteEntity
import com.vasberc.data_local.entities.MovieEntity
import com.vasberc.data_remote.response_model.GetPopularMoviesResponse

data class Movie(
    val backdropPath: String,
    val id: Int,
    val releaseDate: String,
    val title: String,
    val voteAverage: Double,
    val isFavourite: Boolean
) {
    companion object {
        const val LOADING_ITEM_ID = -1000

        fun provideLoadingItem() = Movie(
            "",
            LOADING_ITEM_ID,
            "",
            "",
            0.0,
            false
        )
    }
    fun asEntity(): MovieEntity {
        return MovieEntity(
            backdropPath = backdropPath,
            id = id,
            releaseDate = releaseDate,
            title = title,
            voteAverage = voteAverage
        )
    }

    fun asMovieAndFavoriteEntity(): MovieAndFavoriteEntity {
        return MovieAndFavoriteEntity(
            movieEntity = MovieEntity(
                backdropPath = backdropPath,
                id = id,
                releaseDate = releaseDate,
                title = title,
                voteAverage = voteAverage
            ),
            favouriteEntity = if(isFavourite) FavouriteEntity(id) else null
        )
    }
}

fun MovieAndFavoriteEntity.asMovie(): Movie {
    return Movie(
        backdropPath = movieEntity.backdropPath ?: "",
        id = movieEntity.id,
        releaseDate = movieEntity.releaseDate ?: "",
        title = movieEntity.title ?: "",
        voteAverage = movieEntity.voteAverage ?: 0.0,
        isFavourite = favouriteEntity?.movieId == movieEntity.id
    )
}

fun GetPopularMoviesResponse.Result.asMovie(): Movie {
    return Movie(
        backdropPath = backdropPath ?: "",
        id = id ?: -1,
        releaseDate = releaseDate ?: "",
        title = title ?: "",
        voteAverage = voteAverage ?: 0.0,
        isFavourite = false
    )
}

fun GetPopularMoviesResponse.Result.asMovie(imageBaseUrl: String, imageSize: String): Movie {
    return Movie(
        backdropPath = backdropPath?.let { "$imageBaseUrl$imageSize/$it" } ?: "",
        id = id ?: -1,
        releaseDate = releaseDate ?: "",
        title = title ?: "",
        voteAverage = voteAverage ?: 0.0,
        isFavourite = false
    )
}

fun CachedMovieEntity.asMovie(): Movie {
    return Movie(
        backdropPath = backdropPath ?: "",
        id = id,
        releaseDate = releaseDate ?: "",
        title = title ?: "",
        voteAverage = voteAverage ?: 0.0,
        isFavourite = false
    )
}