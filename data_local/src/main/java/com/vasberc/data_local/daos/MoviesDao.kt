package com.vasberc.data_local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.vasberc.data_local.entities.CachedMovieEntity
import com.vasberc.data_local.entities.CastEntity
import com.vasberc.data_local.entities.FavouriteEntity
import com.vasberc.data_local.entities.GenreEntity
import com.vasberc.data_local.entities.MovieAndFavoriteEntity
import com.vasberc.data_local.entities.MovieCastEntity
import com.vasberc.data_local.entities.MovieCastEntityWithRelations
import com.vasberc.data_local.entities.MovieDetailedEntity
import com.vasberc.data_local.entities.MovieDetailedEntityWithRelations
import com.vasberc.data_local.entities.MovieEntity
import com.vasberc.data_local.entities.MovieGenreEntity
import com.vasberc.data_local.entities.MovieGenreEntityWithRelations
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

@Dao
interface MoviesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavourite(favouriteEntity: FavouriteEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllMovies(movies: List<MovieEntity>)

    @Query("DELETE FROM movies")
    suspend fun clearAllEntities()

    @Query("SELECT * FROM cached_movies")
    suspend fun getCachedMovies(): List<CachedMovieEntity>

    @Transaction
    suspend fun cacheMovies(movies: List<MovieEntity>) {
        //We want to cache only the first page, so always we empty the table
        //to contain only 1 page results
        clearAllCachedMovies()
        insertAllCachedMovies(movies.map { it.asCachedMovie() })
        clearNoNeededCache(movies)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCachedMovies(cachedMovies: List<CachedMovieEntity>)

    @Query("DELETE FROM cached_movies")
    suspend fun clearAllCachedMovies()

    @Transaction
    @Query("SELECT * FROM movies LIMIT :limit OFFSET :offset")
    suspend fun getMoviesByPage(limit: Int, offset: Int): List<MovieAndFavoriteEntity>

    @Transaction
    suspend fun toggleFavorite(movieId: Int) {
        val isCurrentlyFavorite = countFavoritesById(movieId) >= 1
        if(isCurrentlyFavorite) {
            deleteFavoriteById(movieId)
        } else {
            insertFavourite(
                FavouriteEntity(movieId)
            )
        }
    }

    @Query("SELECT COUNT(*) FROM favourites WHERE movieId = :movieId")
    suspend fun countFavoritesById(movieId: Int): Int

    @Query("DELETE FROM favourites WHERE movieId = :movieId")
    suspend fun deleteFavoriteById(movieId: Int)

    @Query("SELECT * FROM favourites WHERE movieId = :movieId LIMIT 1")
    suspend fun getIsFavouriteById(movieId: Int): FavouriteEntity?

    @Transaction
    suspend fun getDetailedCachedMovieById(movieId: Int): MovieDetailedEntityWithRelations? {
        val movieDetailedEntity = getDetailedCachedMovieEntityById(movieId) ?: return null
        val genres = getMovieGenreEntityWithRelationsById(movieId)
        val cast = getMovieCastEntityWithRelationsById(movieId)
        return MovieDetailedEntityWithRelations(
            movieDetailedEntity = movieDetailedEntity,
            movieGenres = genres,
            movieCast = cast
        )
    }

    @Query("SELECT * FROM movies_detailed WHERE id = :movieId")
    suspend fun getDetailedCachedMovieEntityById(movieId: Int): MovieDetailedEntity?

    @Transaction
    @Query("SELECT * FROM movie_genre_entities WHERE movieId = :movieId")
    suspend fun getMovieGenreEntityWithRelationsById(movieId: Int): List<MovieGenreEntityWithRelations>

    @Transaction
    @Query("SELECT * FROM movie_cast_entities WHERE movieId = :movieId")
    suspend fun getMovieCastEntityWithRelationsById(movieId: Int): List<MovieCastEntityWithRelations>

    @Transaction
    suspend fun cacheDetailedMovieWithRelations(movie: MovieDetailedEntityWithRelations) {
        val cached = getNumberOfCachedMoviesById(movie.movieDetailedEntity.id) > 0
        if(cached) {
            cacheDetailedMovie(movie.movieDetailedEntity)
            cacheCast(movie.movieCast)
            cacheGenres(movie.movieGenres)
        }
    }

    @Query("SELECT COUNT(id) FROM cached_movies WHERE id = :movieId")
    suspend fun getNumberOfCachedMoviesById(movieId: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun cacheDetailedMovie(movieDetailedEntity: MovieDetailedEntity)

    @Transaction
    suspend fun cacheCast(cast: List<MovieCastEntityWithRelations>) {
        val movieCastEntities = mutableListOf<MovieCastEntity>()
        val castEntities = mutableListOf<CastEntity>()
        cast.forEach {
            movieCastEntities.add(it.movieCastEntity)
            castEntities.add(it.cast)
        }
        cacheCastEntities(castEntities)
        cacheMovieCastEntities(movieCastEntities)
    }

    @Transaction
    suspend fun cacheGenres(genres: List<MovieGenreEntityWithRelations>) {
        val movieGenreEntities = mutableListOf<MovieGenreEntity>()
        val castEntities = mutableListOf<GenreEntity>()
        genres.forEach {
            movieGenreEntities.add(it.movieGenreEntity)
            castEntities.add(it.genre)
        }
        cacheGenresEntities(castEntities)
        cacheMovieGenresEntities(movieGenreEntities)
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun cacheMovieCastEntities(cast: List<MovieCastEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun cacheMovieGenresEntities(genres: List<MovieGenreEntity>)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun cacheCastEntities(cast: List<CastEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun cacheGenresEntities(genres: List<GenreEntity>)

    /**
     * This function should be called after the caching of the 1st page to clear the data that is no more
     * cached because the 1st page from the network is changed
     */
    @Transaction
    suspend fun clearNoNeededCache(currentCachedMovies: List<MovieEntity>) = coroutineScope {
        launch {
            val cachedMovies = getDetailedCachedMovies()
            val currentCachedMoviesIds = currentCachedMovies.map { it.id }
            val genreIdsToDelete = mutableListOf<Int>()
            val castIdsToDelete = mutableListOf<Int>()
            cachedMovies.filter { !currentCachedMoviesIds.contains(it.id) }.forEach { detailedEntity ->
                val genreEntityWithRelations = getMovieGenreEntityWithRelationsById(detailedEntity.id)
                val castEntityWithRelations = getMovieCastEntityWithRelationsById(detailedEntity.id)
                genreIdsToDelete.addAll(genreEntityWithRelations.map { it.movieGenreEntity.genreId })
                castIdsToDelete.addAll(castEntityWithRelations.map { it.movieCastEntity.castId })
                //Because of the cascade policy also the MovieCastEntities and the MovieGenreEntities
                //that are associated  with the deleted movie will be deleted
                deleteDetailedMovieById(detailedEntity.id)
            }

            genreIdsToDelete.map {
                coroutineScope {
                    launch {
                        try {
                            //Because of the cascade policy restrict, if the genre is associated with a movie that exists
                            //in the current cached movies will not be deleted
                            deleteAllGenres(it)
                        } catch (e: Exception) {
                            Timber.w(e, "Trying to delete genre with id=$it")
                        }
                    }

                }
            }

            castIdsToDelete.map {
                coroutineScope {
                    launch {
                        try {
                            //Because of the cascade policy restrict, if the cast is associated with a movie that exists
                            //in the current cached movies will not be deleted
                            deleteAllCast(it)
                        } catch (e: Exception) {
                            Timber.w(e, "Trying to delete cast with id=$it")
                        }
                    }
                }
            }
        }
    }

    @Query("SELECT * FROM movies_detailed")
    suspend fun getDetailedCachedMovies(): List<MovieDetailedEntity>

    @Query("DELETE FROM movies_detailed WHERE id = :movieId")
    suspend fun deleteDetailedMovieById(movieId: Int)

    @Query("DELETE FROM genres WHERE id = :genreId")
    suspend fun deleteAllGenres(genreId: Int)

    @Query("DELETE FROM `cast` WHERE id = :castId")
    suspend fun deleteAllCast(castId: Int)
}