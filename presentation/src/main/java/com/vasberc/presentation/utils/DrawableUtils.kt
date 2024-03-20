package com.vasberc.presentation.utils

import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable

fun createShimmerDrawable(): ShimmerDrawable {
    val shimmer =
        Shimmer.AlphaHighlightBuilder()// The attributes for a ShimmerDrawable is set by this builder
            .setClipToChildren(true)
            .setTilt(180.0f)
            .setDuration(1000) // how long the shimmering animation takes to do one full sweep
            .setBaseAlpha(0.96f) //the alpha of the underlying children
            .setHighlightAlpha(0.91f) // the shimmer alpha amount
            .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
            .setAutoStart(true)
            .build()

    // This is the placeholder for the imageView
    val shimmerDrawable = ShimmerDrawable().apply {
        setShimmer(shimmer)
    }

    return shimmerDrawable
}