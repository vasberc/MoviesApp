package com.vasberc.presentation.utils

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract  class BaseFragment<BINDING: ViewBinding>(@LayoutRes layout: Int): Fragment(layout) {

    private var _binding: BINDING? = null
    val binding get() = _binding!!

    protected abstract fun createViewBinding(view: View): BINDING

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = createViewBinding(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}