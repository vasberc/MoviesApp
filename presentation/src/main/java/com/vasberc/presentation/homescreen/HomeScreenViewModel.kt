package com.vasberc.presentation.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import com.vasberc.data.models.Movie
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class HomeScreenViewModel(
    moviesPager: Pager<Int, Movie>
): ViewModel() {

    val moviesPagerFlow = moviesPager.flow.cachedIn(viewModelScope)

}