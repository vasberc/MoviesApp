package com.vasberc.presentation.utils

import android.graphics.Color
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable

fun createShimmerDrawable(): ShimmerDrawable {
    val shimmer = Shimmer.ColorHighlightBuilder()// The attributes for a ShimmerDrawable is set by this builder
        .setClipToChildren(true)
        .setBaseAlpha(0.5f)
        .setBaseColor(Color.BLACK)
        .setHighlightAlpha(0.91f) // the shimmer alpha amount
        .setTilt(195.0f)
        .setDuration(800) // how long the shimmering animation takes to do one full sweep
        .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
        .setAutoStart(true)
        .build()

    // This is the placeholder for the imageView
    val shimmerDrawable = ShimmerDrawable().apply {
        setShimmer(shimmer)
    }

    return shimmerDrawable
}