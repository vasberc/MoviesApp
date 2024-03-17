package com.vasberc.presentation.di

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import com.vasberc.data.models.Movie
import com.vasberc.data.paging.MoviesRemoteMediator
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module

@Module
@ComponentScan("com.vasberc.presentation")
class PresentationModule

@OptIn(ExperimentalPagingApi::class)
@Factory
fun provideMoviePager(pagingSourceFactory: () -> PagingSource<Int,Movie>, remoteMediator: MoviesRemoteMediator): Pager<Int,Movie> {
    return Pager(
        config = PagingConfig(
            prefetchDistance = 5,
            enablePlaceholders = true,
            pageSize = 20
        ),
        remoteMediator = remoteMediator,
        pagingSourceFactory = pagingSourceFactory
    )
}