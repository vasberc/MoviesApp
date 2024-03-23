package com.vasberc.data_local.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation

data class MovieDetailedEntityWithRelations(
    val movieDetailedEntity: MovieDetailedEntity,
    val movieCast: List<MovieCastEntityWithRelations>,
    val movieGenres: List<MovieGenreEntityWithRelations>
)

@Entity(tableName = "movies_detailed")
data class MovieDetailedEntity(
    val backdropPath: String,
    val homepage: String,
    @PrimaryKey
    val id: Int,
    val releaseDate: String,
    val runtime: Int,
    val title: String,
    val voteAverage: Double,
    val isFavourite: Boolean,
    val overview: String
)

data class MovieCastEntityWithRelations(
    @Embedded
    val movieCastEntity: MovieCastEntity,
    @Relation(
        parentColumn = "castId",
        entityColumn = "id"
    )
    val cast: CastEntity
)

data class MovieGenreEntityWithRelations(
    @Embedded
    val movieGenreEntity: MovieGenreEntity,
    @Relation(
        parentColumn = "genreId",
        entityColumn = "id"
    )
    val genre: GenreEntity
)

@Entity(
    tableName = "movie_cast_entities",
    primaryKeys = ["movieId", "castId"],
    foreignKeys = [
        ForeignKey(
            entity = MovieDetailedEntity::class,
            parentColumns = ["id"],
            childColumns = ["movieId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CastEntity::class,
            parentColumns = ["id"],
            childColumns = ["castId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class MovieCastEntity(
    val movieId: Int,
    val castId: Int
)

@Entity(
    tableName = "movie_genre_entities",
    primaryKeys = ["movieId", "genreId"],
    foreignKeys = [
        ForeignKey(
            entity = MovieDetailedEntity::class,
            parentColumns = ["id"],
            childColumns = ["movieId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = GenreEntity::class,
            parentColumns = ["id"],
            childColumns = ["genreId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class MovieGenreEntity(
    val movieId: Int,
    val genreId: Int
)

@Entity("cast")
data class CastEntity(
    @PrimaryKey
    val id: Int,
    val name: String
)

@Entity("genres")
data class GenreEntity(
    @PrimaryKey
    val id: Int,
    val name: String
)