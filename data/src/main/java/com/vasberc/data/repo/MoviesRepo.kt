package com.vasberc.data.repo

import com.vasberc.data.models.Movie

interface MoviesRepo {
    fun toggleFavourite(movie: Movie)
}