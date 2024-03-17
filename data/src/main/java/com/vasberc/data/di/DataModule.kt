package com.vasberc.data.di

import androidx.paging.PagingSource
import com.vasberc.data.models.Movie
import com.vasberc.data.models.asMovie
import com.vasberc.data.paging.MoviesRemoteMediator
import com.vasberc.data_local.daos.MovieRemoteKeysDao
import com.vasberc.data_local.daos.MoviesDao
import com.vasberc.data_remote.service.MoviesService
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module

@Module
@ComponentScan("com.vasberc.data")
class DataModule

@Factory
fun provideMoviePagingSource(moviesDao: MoviesDao): () -> PagingSource<Int, Movie> {
    return moviesDao.getMovies().map {
        it.asMovie()
    }.asPagingSourceFactory()
}

@Factory
fun provideMoviesRemoteMediator(moviesDao: MoviesDao, movieRemoteKeysDao: MovieRemoteKeysDao, service: MoviesService): MoviesRemoteMediator {
    return MoviesRemoteMediator(moviesDao, movieRemoteKeysDao, service)
}