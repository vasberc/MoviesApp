package com.vasberc.data_local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.vasberc.data_local.entities.CachedMovieEntity
import com.vasberc.data_local.entities.FavouriteEntity
import com.vasberc.data_local.entities.MovieAndFavoriteEntity
import com.vasberc.data_local.entities.MovieEntity

@Dao
interface MoviesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavourite(favouriteEntity: FavouriteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
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
        resetCachedMoviesAutoIncrement()
        insertAllCachedMovies(movies.map { it.asCachedMovie() })
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCachedMovies(cachedMovies: List<CachedMovieEntity>)

    @Query("DELETE FROM cached_movies")
    suspend fun clearAllCachedMovies()

    @Query("UPDATE sqlite_sequence SET seq = 0 WHERE name = \'movies\'")
    suspend fun resetMoviesAutoIncrement()

    @Query("UPDATE sqlite_sequence SET seq = 0 WHERE name = \'cached_movies\'")
    suspend fun resetCachedMoviesAutoIncrement()

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
}