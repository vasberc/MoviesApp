package com.vasberc.presentation.homescreen

import android.view.View
import com.vasberc.presentation.R
import com.vasberc.presentation.databinding.HomeScreenFragmentBinding
import com.vasberc.presentation.utils.BaseFragment

class HomeScreenFragment: BaseFragment<HomeScreenFragmentBinding>(R.layout.home_screen_fragment) {
    override fun createViewBinding(view: View): HomeScreenFragmentBinding {
        return HomeScreenFragmentBinding.bind(view)
    }
}