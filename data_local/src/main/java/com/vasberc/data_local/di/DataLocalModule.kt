package com.vasberc.data_local.di

import android.content.Context
import androidx.room.Room
import com.vasberc.data_local.daos.MovieRemoteKeysDao
import com.vasberc.data_local.daos.MoviesDao
import com.vasberc.data_local.db.MovieFlixDataBase
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.vasberc.data_local")
class DataLocalModule

@Single
fun provideDb(context: Context): MovieFlixDataBase {
    return Room.databaseBuilder(
        context,
        MovieFlixDataBase::class.java,
        "movie_flix_db"
    ).build()
}

@Single
fun provideMovieRemoteKeysDao(dataBase: MovieFlixDataBase): MovieRemoteKeysDao {
    return dataBase.movieRemoteKeysDao()
}

@Single
fun provideMoviesDaoDao(dataBase: MovieFlixDataBase): MoviesDao {
    return dataBase.moviesDao()
}