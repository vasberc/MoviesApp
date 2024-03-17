package com.vasberc.data.repo

import com.vasberc.data.models.ErrorModel
import com.vasberc.data.models.ImagesConfiguration
import com.vasberc.data.models.asImagesConfiguration
import com.vasberc.data.utils.ResultState
import com.vasberc.data.utils.parseResponse
import com.vasberc.data_local.repo.MoviesLocalRepo
import com.vasberc.data_remote.repo.MoviesRemoteRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.koin.core.annotation.Single

@Single
class MoviesRepoImpl(moviesLocalRepo: MoviesLocalRepo, private val moviesRemoteRepo: MoviesRemoteRepo): MoviesRepo {
}