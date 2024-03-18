package com.vasberc.presentation.utils

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract  class BaseFragment<BINDING: ViewBinding>(@LayoutRes layout: Int): Fragment(layout) {

    @get:LayoutRes
    protected open val supportActionBar: Int? = null
    protected open val homeAsUp: Boolean = false

    private var _binding: BINDING? = null
    val binding get() = _binding!!

    protected abstract fun createViewBinding(view: View): BINDING

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = createViewBinding(view)
        super.onViewCreated(view, savedInstanceState)
        if (supportActionBar != null) {
            (activity as? AppCompatActivity)?.setSupportActionBar(view.findViewById(supportActionBar!!))
        }
        val supportActionBar = (activity as? AppCompatActivity)?.supportActionBar

        if (homeAsUp) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}