package com.vasberc.presentation.homescreen

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.vasberc.presentation.R
import com.vasberc.presentation.databinding.HomeScreenFragmentBinding
import com.vasberc.presentation.utils.BaseFragment
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeScreenFragment: BaseFragment<HomeScreenFragmentBinding>(R.layout.home_screen_fragment) {

    private val viewModel: HomeScreenViewModel by viewModel()
    private val adapter: PopularMoviesPagingAdapter? get() {
        return try {
            binding.rvPopularMovies.adapter as PopularMoviesPagingAdapter
        } catch (e: Exception) {
            null
        }
    }

    override fun createViewBinding(view: View): HomeScreenFragmentBinding {
        return HomeScreenFragmentBinding.bind(view)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        setUpFlowCollectors()
        setUpPullToRefresh()
    }

    private fun setUpPullToRefresh() {
        binding.root.setOnRefreshListener {
            adapter?.refresh()
        }
    }

    private fun setUpRecyclerView() {
        if(adapter == null) {
            binding.rvPopularMovies.adapter = PopularMoviesPagingAdapter()
            binding.rvPopularMovies.layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setUpFlowCollectors() {
        //We use the viewLifecycleOwner lifecycle scope because we are launching the collectors
        //every time the onViewCreated is invoked and this way we avoid to launch multiple
        //collectors for the same job
        viewModel.moviesPagerFlow.onEach {
            adapter?.submitData(viewLifecycleOwner.lifecycle, it)
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        adapter?.loadStateFlow?.onEach { combinedLoadStates ->
            val mediatorStates = combinedLoadStates.mediator
            val sourceStates = combinedLoadStates.source
            if((sourceStates.refresh is LoadState.Loading && mediatorStates?.prepend?.endOfPaginationReached == false) ||
                mediatorStates?.refresh is LoadState.Loading) {
                //Refresh loading state, data is not presented to the ui
                //hide the small spinner of the pull to refresh component
                binding.root.isRefreshing = false
            }
        }?.launchIn(viewLifecycleOwner.lifecycleScope)
    }
}