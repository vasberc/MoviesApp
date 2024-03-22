package com.vasberc.presentation.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import com.vasberc.data.models.Movie
import com.vasberc.data.repo.MoviesRepo
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class HomeScreenViewModel(
    moviesPager: Pager<Int, Movie>,
    private val moviesRepo: MoviesRepo
): ViewModel() {

    val moviesPagerFlow = moviesPager.flow.cachedIn(viewModelScope)

    fun toggleFavourite(movie: Movie) {
        viewModelScope.launch {
            moviesRepo.toggleFavourite(movie.id)
        }
    }
}