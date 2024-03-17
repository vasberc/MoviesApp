package com.vasberc.presentation.homescreen

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.vasberc.presentation.R
import com.vasberc.presentation.databinding.HomeScreenFragmentBinding
import com.vasberc.presentation.utils.BaseFragment
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeScreenFragment: BaseFragment<HomeScreenFragmentBinding>(R.layout.home_screen_fragment) {

    private val viewModel: HomeScreenViewModel by viewModel()

    private var _adapter: PopularMoviesPagingAdapter? = null
    private val adapter: PopularMoviesPagingAdapter get() {
        if(_adapter == null) {
            _adapter = PopularMoviesPagingAdapter()
        }
        return _adapter!!
    }

    override fun createViewBinding(view: View): HomeScreenFragmentBinding {
        return HomeScreenFragmentBinding.bind(view)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        setUpFlowCollectors()
    }

    private fun setUpRecyclerView() {
        if(binding.rvPopularMovies.adapter == null) {
            binding.rvPopularMovies.adapter = adapter.withLoadStateFooter(
                MediatorLoadStateAdapter {
                    adapter.retry()
                }
            )
            binding.rvPopularMovies.layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setUpFlowCollectors() {
        //We use the viewLifecycleOwner lifecycle scope because we are launching the collectors
        //every time the onViewCreated is invoked and this way we avoid to launch multiple
        //collectors for the same job
        viewModel.moviesPagerFlow.onEach {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        adapter.loadStateFlow.onEach { combinedLoadStates ->

        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _adapter = null
    }
}