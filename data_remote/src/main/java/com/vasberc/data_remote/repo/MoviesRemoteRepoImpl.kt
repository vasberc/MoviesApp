package com.vasberc.data_remote.repo

import com.vasberc.data_remote.service.MoviesService
import org.koin.core.annotation.Single

@Single
class MoviesRemoteRepoImpl(private val service: MoviesService):
    MoviesRemoteRepo {


}