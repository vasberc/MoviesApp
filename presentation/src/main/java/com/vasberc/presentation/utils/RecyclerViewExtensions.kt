package com.vasberc.presentation.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

fun RecyclerView.smoothScrollTargetElement(position: Int) {

    val positionCorrector = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            try {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    recyclerView.removeOnScrollListener(this)
                    val firstVisibleItem = (layoutManager as? LinearLayoutManager)?.findFirstCompletelyVisibleItemPosition()
                    if(firstVisibleItem != position) {
                        scrollToPosition(position)
                    }

                }
            } catch (e: Exception) {
                Timber.w(e)
            }
        }
    }

    // add positionCorrector to fix the position after scrolling
    addOnScrollListener(positionCorrector)
    // start scroll
    val firstVisibleItem = (layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition() ?: 0
    val scrollTo = (firstVisibleItem - 10).coerceAtLeast(0)
    smoothScrollToPosition(scrollTo)
}