package com.vasberc.presentation.webview

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.vasberc.presentation.R
import com.vasberc.presentation.databinding.WebViewFragmentBinding
import com.vasberc.presentation.utils.BaseFragment

class WebViewFragment: BaseFragment<WebViewFragmentBinding>(R.layout.web_view_fragment) {

    override val supportActionBar: Int = R.id.tool_bar

    override fun createViewBinding(view: View): WebViewFragmentBinding {
        return WebViewFragmentBinding.bind(view)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.webView.webViewClient = object : WebViewClient() { }
        arguments?.getString("url")?.let { binding.webView.loadUrl(it) }

        binding.ivRefresh.setOnClickListener {
            binding.webView.reload()
        }

        binding.ivClose.setOnClickListener {
            activity?.onBackPressed()
        }
    }
}