package com.vasberc.data_local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vasberc.data_local.daos.MoviesDao
import com.vasberc.data_local.entities.CachedMovieEntity
import com.vasberc.data_local.entities.FavouriteEntity
import com.vasberc.data_local.entities.MovieEntity
import org.koin.core.annotation.Single

@Database(
    entities = [FavouriteEntity::class, CachedMovieEntity::class, MovieEntity::class],
    exportSchema = false,
    version = 1
)
abstract class MovieFlixDataBase: RoomDatabase() {

    @Single
    abstract fun moviesDao(): MoviesDao
}