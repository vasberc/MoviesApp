package com.vasberc.presentation.utils

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import timber.log.Timber

abstract  class BaseFragment<BINDING: ViewBinding>(@LayoutRes layout: Int): Fragment(layout) {

    @get:LayoutRes
    protected open val supportActionBar: Int? = null
    protected open val homeAsUp: Boolean = false
    protected open val title: String = ""
    @get:LayoutRes
    protected open val titleId: Int? = null

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

        supportActionBar?.title = titleId?.let { getString(it) } ?: title

        if (homeAsUp) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }
    }

    protected fun navigate(
        directions: NavDirections,
        navOptions: NavOptions? = null
    ) {
        try {
            findNavController().navigate(directions, navOptions)
        } catch (ex: IllegalArgumentException) {
            Timber.w(ex, "trying to navigate")
        } catch (e: Exception) {
            throw e
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}