package com.vasberc.data.repo

import com.vasberc.data.models.Movie
import com.vasberc.data_local.repo.MoviesLocalRepo
import com.vasberc.data_remote.repo.MoviesRemoteRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

@Single
class MoviesRepoImpl(private val moviesLocalRepo: MoviesLocalRepo, private val moviesRemoteRepo: MoviesRemoteRepo): MoviesRepo {
    override fun toggleFavourite(movie: Movie) {
        CoroutineScope(Dispatchers.IO).launch {
            moviesLocalRepo.toggleFavourite(movie.id)
        }
    }
}