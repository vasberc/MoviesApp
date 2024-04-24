package com.vasberc.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.vasberc.data.configuration.Configuration
import com.vasberc.data.models.Movie
import com.vasberc.data.models.asErrorModel
import com.vasberc.data.models.asImagesConfiguration
import com.vasberc.data.models.asMovie
import com.vasberc.data.utils.ResultState
import com.vasberc.data.utils.parseResponse
import com.vasberc.data_local.daos.MovieRemoteKeysDao
import com.vasberc.data_local.daos.MoviesDao
import com.vasberc.data_local.entities.MovieRemoteKeysEntity
import com.vasberc.data_remote.service.MoviesService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

@OptIn(ExperimentalPagingApi::class)
class MoviesRemoteMediator(private val moviesDao: MoviesDao, private val remoteKeysDao: MovieRemoteKeysDao, private val service: MoviesService): RemoteMediator<Int, Movie>() {

    var remoteDataTotalItems: Int? = null

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Movie>
    ): MediatorResult {
       return withContext(Dispatchers.IO)  {
           Timber.d("MoviesRemoteMediator new loadState=$loadType")
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    //Set to null, so the paging source will know that the state is loading
                    remoteDataTotalItems = null
                    //There refresh is our implementation can be triggered only in the top
                    //element when the user makes pull to refresh, so we have to clear the data
                    //because we will get from network all the data again
                    remoteKeysDao.clearRemoteKeys()
                    moviesDao.clearAllEntities()
                    if(Configuration.images == null) {
                        getImagesConfiguration()
                    }
                    1
                }
                LoadType.PREPEND -> {
                    return@withContext MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    // If remoteKeys is null, that means the refresh result is not in the database yet.
                    // We can return Success with endOfPaginationReached = false because Paging
                    // will call this method again if RemoteKeys becomes non-null.
                    // If remoteKeys is NOT NULL but its nextKey is null, that means we've reached
                    // the end of pagination for append.
                    val nextKey = remoteKeys?.nextKey
                        ?: return@withContext MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    nextKey
                }
            }

           val result = try {
               handleSuccessFulResponse(page)
           } catch (e: Exception) {
               Timber.w(e, "MoviesRemoteMediator error page=$page")
               //We could not connect with the api at all, we need to show the cashed movies if
               //the load was for the first page
               if(page == 1) {
                   handleConnectionErrorOnFirstPageLoad(page, e)

               } else {
                   MediatorResult.Error(e)
               }
           }
           Timber.d("MoviesRemoteMediator loadType=$loadType page=$page result=$result")
            return@withContext withContext(Dispatchers.Main.immediate) {
                result
            }
        }
    }

    private suspend fun getImagesConfiguration() {
        val imagesConfigurationResponse = service.getConfiguration().parseResponse(
            successMapper = { asImagesConfiguration() },
            serverErrorMapper = { errorCode ->
                asErrorModel(errorCode)
            }
        )
        if(imagesConfigurationResponse is ResultState.Success) {
            Configuration.images = imagesConfigurationResponse.data
        }
    }

    private suspend fun handleSuccessFulResponse(page: Int): MediatorResult {
        val response = service.getPopularMovies("en-US", page)
        //When the response is successful we save the results in the data to trigger the paging
        //source and refresh the ui
        val totalPages = response.totalPages
        val movies = response.results?.asSequence()?.mapNotNull {
            it?.asMovie(
                imageBaseUrl = Configuration.images?.secureBaseUrl ?: "",
                imageSize = Configuration.images?.backdropSizes?.get(2) ?: ""
            )
        }?.toList()
            ?: listOf()
        val endOfPagination = page == totalPages || movies.isEmpty()
        remoteDataTotalItems = response.totalResults
        saveResults(movies, endOfPagination, page)
        Timber.d("MoviesRemoteMediator success page=$page, pageSize=${movies.size}, endOfPagination=$endOfPagination")
        return MediatorResult.Success(endOfPaginationReached = endOfPagination)
    }

    private suspend inline fun handleConnectionErrorOnFirstPageLoad(
        page: Int,
        e: Exception
    ): MediatorResult {

        val cachedEntities = moviesDao.getCachedMovies().map { it.asMovie() }

        return if (cachedEntities.size > 1) {
            //Adding one to the total item, so the paging source will make the adapter to show a loading placeholder
            //this way the user will understand that there is a loading issue and will try to make a pull to refresh
            //to fix it
            remoteDataTotalItems = cachedEntities.size + 1

            saveResults(cachedEntities, true, page)
            MediatorResult.Success(endOfPaginationReached = true)
        } else {
            //If there are not cached movies return error
            MediatorResult.Error(e)
        }
    }

    private suspend fun saveResults(
        movies: List<Movie>,
        endOfPagination: Boolean,
        page: Int
    ) {
        val prevPage = (page - 1).takeUnless { it <= 0 }
        val nextPage = (page + 1).takeUnless { endOfPagination }
        val remoteKeys = movies.map { MovieRemoteKeysEntity(it.id, prevPage, nextPage) }
        val movieEntities = movies.mapIndexed { index, movie ->
            movie.asEntity(
                position = 20 * (page - 1) + index
            )
        }
        Timber.d("Hellooooooooo Ready to save new data")
        remoteKeysDao.insertAll(remoteKeys)
        moviesDao.insertAllMovies(movieEntities)
        //Every time we load the 1st page, we cache the response
        if(page == 1) {
            //To not delay the mediator result we launch a new coroutine so the caching can be
            //done async
            CoroutineScope(Dispatchers.IO).launch {
                moviesDao.cacheMovies(movieEntities)
            }
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Movie>): MovieRemoteKeysEntity? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { movie ->
                // Get the remote keys of the last item retrieved
               remoteKeysDao.remoteKeysRepoId(movie.id)
            }
    }
}