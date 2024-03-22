package com.vasberc.presentation.homescreen

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.vasberc.presentation.R
import com.vasberc.presentation.databinding.HomeScreenFragmentBinding
import com.vasberc.presentation.utils.BaseFragment
import com.vasberc.presentation.utils.smoothScrollTargetElement
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeScreenFragment: BaseFragment<HomeScreenFragmentBinding>(R.layout.home_screen_fragment) {

    private val viewModel: HomeScreenViewModel by viewModel()
    private var retryJob: Job? = null
    private val adapter: PopularMoviesPagingAdapter? get() {
        return try {
            binding.rvPopularMovies.adapter as PopularMoviesPagingAdapter
        } catch (e: Exception) {
            null
        }
    }

    private val layoutManager: LinearLayoutManager? get() {
        return try {
            binding.rvPopularMovies.layoutManager as LinearLayoutManager
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
        setUpListeners()
    }

    private fun setUpListeners() {
        binding.rvPopularMovies.setOnScrollChangeListener { _, _, _, _, _ ->
            if(layoutManager != null) {
                val firstVisibleItem = layoutManager!!.findFirstVisibleItemPosition()
                if(firstVisibleItem > 0 &&  !binding.fabScrollToTop.isVisible) {
                    binding.fabScrollToTop.isVisible = true
                } else if(firstVisibleItem == 0 && binding.fabScrollToTop.isVisible) {
                    binding.fabScrollToTop.isVisible = false
                }
            }

            binding.fabScrollToTop.setOnClickListener {
                binding.rvPopularMovies.smoothScrollTargetElement(0)
            }
        }

        adapter?.favouriteClicks?.onEach {
            viewModel.toggleFavourite(it)
        }?.launchIn(viewLifecycleOwner.lifecycleScope)

        adapter?.rootClicks?.onEach {
           navigate(HomeScreenFragmentDirections.actionHomeScreenFragmentToMovieDetailedFragment(
               movieId = it.id,
               title = it.title
           ))
        }?.launchIn(viewLifecycleOwner.lifecycleScope)
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
            //disable items flashing when the adapter is reDrawing the viewHolder's view
            (binding.rvPopularMovies.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
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
            } else if(mediatorStates?.append is LoadState.Error) {
                retryJob?.cancel()
                //In case a page could not be loaded due to error, try for 5 times after 10 seconds
                // to get the failed page
                retryJob = viewLifecycleOwner.lifecycleScope.launch {
                    for(i in 0..5) {
                        delay(10000)
                        adapter?.retry()
                    }
                }
            } else {
                retryJob?.cancel()
                retryJob = null
            }
        }?.launchIn(viewLifecycleOwner.lifecycleScope)
    }
}