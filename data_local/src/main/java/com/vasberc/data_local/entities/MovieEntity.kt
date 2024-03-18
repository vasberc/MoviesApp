package com.vasberc.data_local.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "movies", indices = [Index(value = ["id"], unique = true)])
data class MovieEntity(
    val backdropPath: String?,
    val id: Int,
    val releaseDate: String?,
    val title: String?,
    val voteAverage: Double?,
    //Because the room is reordering the elements in the db based on the primary key,
    //we use the position field to keep the elements in the ui is the same position,
    //in every load
    @PrimaryKey
    val position: Int
) {
     fun asCachedMovie(): CachedMovieEntity {
         return CachedMovieEntity(backdropPath, id, releaseDate, title, voteAverage, position)
     }
}

@Entity(tableName = "cached_movies")
data class CachedMovieEntity(
    val backdropPath: String?,
    val id: Int,
    val releaseDate: String?,
    val title: String?,
    val voteAverage: Double?,
    //Because the room is reordering the elements in the db based on the primary key,
    //we use the position field to keep the elements in the ui is the same position,
    //in every load
    @PrimaryKey
    val position: Int
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