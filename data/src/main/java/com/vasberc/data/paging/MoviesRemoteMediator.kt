package com.vasberc.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.vasberc.data.models.Movie
import com.vasberc.data.models.asMovie
import com.vasberc.data_local.daos.MovieRemoteKeysDao
import com.vasberc.data_local.daos.MoviesDao
import com.vasberc.data_local.entities.MovieRemoteKeysEntity
import com.vasberc.data_remote.response_model.GetPopularMoviesResponse
import com.vasberc.data_remote.service.MoviesService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import retrofit2.Response
import timber.log.Timber

@OptIn(ExperimentalPagingApi::class)
class MoviesRemoteMediator(private val moviesDao: MoviesDao, private val remoteKeysDao: MovieRemoteKeysDao, private val service: MoviesService): RemoteMediator<Int, Movie>() {

    private val mutex = Mutex()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Movie>
    ): MediatorResult {
       return withContext(Dispatchers.IO)  {
           Timber.d("MoviesRemoteMediator new loadState=$loadType")
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    //There refresh is our implementation can be triggered only in the top
                    //element when the user makes pull to refresh, so we have to clear the data
                    //because we will get from network all the data again
                    remoteKeysDao.clearRemoteKeys()
                    moviesDao.clearAllEntities()
                    //Reset the auto increment in every refresh because the table is empty
                    moviesDao.resetMoviesAutoIncrement()
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
                val response = service.getPopularMovies("en-US", page)
                if(response.isSuccessful) {
                    handleSuccessfulResponse(response, page)
                } else {
                    handleServerErrorResponse(response, page)
                }
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

            return@withContext withContext(Dispatchers.Main.immediate) {
                result
            }
        }
    }

    private suspend inline fun handleConnectionErrorOnFirstPageLoad(
        page: Int,
        e: Exception
    ): MediatorResult {
        //Getting the cached movies and adding at the end an item that the adapter will display
        //always the shimmer loading, so the user will understand that there is a loading issue and
        //they will try to pull to refresh the view
        val cachedEntities = (moviesDao.getCachedMovies()
            .asSequence().map { it.asMovie() } + Movie.provideLoadingItem()).toList()

        return if (cachedEntities.size > 1) {
            //Not giving end of pagination, so the user will be able to scroll down and get the next page
            saveResults(cachedEntities, true, page)
            MediatorResult.Success(endOfPaginationReached = true)
        } else {
            //If there are not cached movies return error
            MediatorResult.Error(e)
        }
    }

    private fun handleServerErrorResponse(
        response: Response<GetPopularMoviesResponse>,
        page: Int
    ): MediatorResult.Error {
        //This is an error response from the server, we do not save anything, the paging
        //adapter will handle this
        val exception = Exception(response.errorBody()?.toString())
        Timber.w(exception, "MoviesRemoteMediator error page=$page")
        return MediatorResult.Error(exception)
    }

    private suspend inline fun handleSuccessfulResponse(
        response: Response<GetPopularMoviesResponse>,
        page: Int
    ): MediatorResult.Success {
        //When the response is successful we save the results in the data to trigger the paging
        //source and refresh the ui
        val totalPages = response.body()?.totalPages
        val movies = response.body()?.results?.asSequence()?.mapNotNull { it?.asMovie() }?.toList()
            ?: listOf()
        val endOfPagination = page == totalPages || movies.isEmpty()
        saveResults(movies, endOfPagination, page)
        Timber.d("MoviesRemoteMediator success page=$page, pageSize=${movies.size}, endOfPagination=$endOfPagination")
        return MediatorResult.Success(endOfPaginationReached = endOfPagination)
    }

    private suspend fun saveResults(
        movies: List<Movie>,
        endOfPagination: Boolean,
        page: Int
    ) {
        val prevPage = (page - 1).takeUnless { it <= 0 }
        val nextPage = (page + 1).takeUnless { endOfPagination }
        val remoteKeys = movies.map { MovieRemoteKeysEntity(it.id, prevPage, nextPage) }
        val movieEntities = movies.map { it.asEntity() }
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