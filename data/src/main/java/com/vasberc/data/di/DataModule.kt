package com.vasberc.data.di

import com.vasberc.data.paging.MoviesPagingSource
import com.vasberc.data.paging.MoviesRemoteMediator
import com.vasberc.data_local.daos.MovieRemoteKeysDao
import com.vasberc.data_local.daos.MoviesDao
import com.vasberc.data_local.db.MovieFlixDataBase
import com.vasberc.data_remote.service.MoviesService
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.vasberc.data")
class DataModule

@Single
fun provideMoviePagingSource(dataBase: MovieFlixDataBase, mediator: MoviesRemoteMediator): () -> MoviesPagingSource {
    return {
        MoviesPagingSource(dataBase, mediator.remoteDataTotalItems)
    }
}

@Single
fun provideMoviesRemoteMediator(moviesDao: MoviesDao, movieRemoteKeysDao: MovieRemoteKeysDao, service: MoviesService): MoviesRemoteMediator {
    return MoviesRemoteMediator(moviesDao, movieRemoteKeysDao, service)
}