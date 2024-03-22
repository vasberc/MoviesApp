package com.vasberc.data_local.repo

interface MoviesLocalRepo {
    suspend fun toggleFavourite(movieId: Int)
    suspend fun checkFavouriteById(movieId: Int): Boolean
}