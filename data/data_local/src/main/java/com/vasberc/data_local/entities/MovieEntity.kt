package com.vasberc.data_local.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "movies")
data class MovieEntity(
    val backdropPath: String?,
    @PrimaryKey
    val id: Int,
    val releaseDate: String?,
    val title: String?,
    val voteAverage: Double?
)

@Entity(tableName = "cached_movies")
data class CachedMovieEntity(
    val backdropPath: String?,
    @PrimaryKey
    val id: Int,
    val releaseDate: String?,
    val title: String?,
    val voteAverage: Double?
)

data class MovieAndFavoriteEntity(
    @Embedded
    val movieEntity: MovieEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "movieId"
    )
    val favouriteEntity: FavouriteEntity?
)