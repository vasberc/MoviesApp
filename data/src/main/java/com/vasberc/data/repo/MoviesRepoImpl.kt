package com.vasberc.data.repo

import com.vasberc.data_local.repo.MoviesLocalRepo
import com.vasberc.data_remote.repo.MoviesRemoteRepo
import org.koin.core.annotation.Single

@Single
class MoviesRepoImpl(moviesLocalRepo: MoviesLocalRepo, moviesRemoteRepo: MoviesRemoteRepo): MoviesRepo {

}