package com.vasberc.data_local.daos

import androidx.paging.DataSource
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

    @Transaction
    @Query("SELECT * FROM movies")
    fun getMovies(): DataSource.Factory<Int, MovieAndFavoriteEntity>

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
        insertAllCachedMovies(movies.map { it.asCachedMovie() })
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCachedMovies(cachedMovies: List<CachedMovieEntity>)

    @Query("DELETE FROM cached_movies")
    suspend fun clearAllCachedMovies()

    @Query("UPDATE sqlite_sequence SET seq = 0 WHERE name = \'movies\'")
    suspend fun resetMoviesAutoIncrement()
}