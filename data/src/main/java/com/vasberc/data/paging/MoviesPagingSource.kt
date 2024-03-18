package com.vasberc.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.room.InvalidationTracker
import com.vasberc.data.models.Movie
import com.vasberc.data.models.asMovie
import com.vasberc.data.utils.Quadruple
import com.vasberc.data_local.db.MovieFlixDataBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class MoviesPagingSource(private val db: MovieFlixDataBase, private val remoteDataTotalItems: Int?): PagingSource<Int, Movie>() {

    init {
        db.invalidationTracker.addObserver(object : InvalidationTracker.Observer(arrayOf("movies", "favourites")) {
            override fun onInvalidated(tables: Set<String>) {
                invalidate()
                //Remove this observer because when the invalidate is invoked pager creates a new instance of the paging source
                db.invalidationTracker.removeObserver(this)
            }
        })
    }
    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        // Try to find the page key of the closest page to anchorPosition, from
        // either the prevKey or the nextKey, but you need to handle nullability
        // here:
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey null -> anchorPage is the initial page, so
        //    just return null.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return try {
            val (movies, page, itemsBefore, itemsAfter) = withContext(Dispatchers.IO) {
                val page = params.key ?: 1
                val limit = params.loadSize
                val offset = (page - 1) * limit
                val movies = db.moviesDao().getMoviesByPage(limit, offset).map { it.asMovie() }
                val itemsBefore = params.loadSize * (page -1)
                //Setting items after to 100 if the total items is null, because
                //null means that is the initial load of the list, and we will insert 100 loading place holders
                //tha they will display the shimmer loading item
                val itemsAfter = (remoteDataTotalItems ?: 100) - itemsBefore - movies.size
                Quadruple(movies, page, itemsBefore, itemsAfter)
            }
            Timber.d("results $movies")
            LoadResult.Page(
                data = movies,
                prevKey = if(page == 1) null else page - 1,
                nextKey = if(movies.isEmpty()) null else page + 1,
                itemsBefore = itemsBefore,
                itemsAfter = itemsAfter
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}