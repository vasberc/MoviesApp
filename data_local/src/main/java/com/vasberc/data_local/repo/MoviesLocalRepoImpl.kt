package com.vasberc.data_local.repo

import com.vasberc.data_local.daos.MoviesDao
import org.koin.core.annotation.Single

@Single
class MoviesLocalRepoImpl(private val moviesDao: MoviesDao): MoviesLocalRepo {
    override suspend fun toggleFavourite(movieId: Int) {
        moviesDao.toggleFavorite(movieId)
    }

    override suspend fun checkFavouriteById(movieId: Int): Boolean {
        return moviesDao.getIsFavouriteById(movieId) != null
    }
}