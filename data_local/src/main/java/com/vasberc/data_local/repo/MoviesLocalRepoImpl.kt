package com.vasberc.data_local.repo

import com.vasberc.data_local.daos.MoviesDao
import com.vasberc.data_local.entities.MovieDetailedEntityWithRelations
import org.koin.core.annotation.Single

@Single
class MoviesLocalRepoImpl(private val moviesDao: MoviesDao): MoviesLocalRepo {
    override suspend fun toggleFavourite(movieId: Int) {
        moviesDao.toggleFavorite(movieId)
    }

    override suspend fun checkFavouriteById(movieId: Int): Boolean {
        return moviesDao.getIsFavouriteById(movieId) != null
    }

    override suspend fun getDetailedCachedMovieById(movieId: Int): MovieDetailedEntityWithRelations? {
        return moviesDao.getDetailedCachedMovieById(movieId)
    }

    override suspend fun cacheDetaildMovie(movieDetailedEntityWithRelations: MovieDetailedEntityWithRelations) {
        moviesDao.cacheDetailedMovieWithRelations(movieDetailedEntityWithRelations)
    }
}