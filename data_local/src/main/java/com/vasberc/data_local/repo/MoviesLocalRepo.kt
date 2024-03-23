package com.vasberc.data_local.repo

import com.vasberc.data_local.entities.MovieDetailedEntityWithRelations

interface MoviesLocalRepo {
    suspend fun toggleFavourite(movieId: Int)
    suspend fun checkFavouriteById(movieId: Int): Boolean
    suspend fun getDetailedCachedMovieById(movieId: Int): MovieDetailedEntityWithRelations?
    suspend fun cacheDetaildMovie(movieDetailedEntityWithRelations: MovieDetailedEntityWithRelations)
}