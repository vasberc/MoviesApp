package com.vasberc.presentation.utils

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import coil.load
import coil.size.Scale
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.vasberc.presentation.R

fun ImageView.loadFromNetWork(
    imageUrl: String?,
    loadingDrawable: Drawable,
    errorDrawable: Int
) {
    if(imageUrl?.isNotEmpty() == true) {
        load(imageUrl) {
            placeholder(loadingDrawable)
            error(errorDrawable)
            scale(Scale.FILL)
        }
    } else {
        load(errorDrawable)
    }

}

fun ShapeableImageView.setCornersRadius(
    topLeft: Boolean = false,
    topRight: Boolean = false,
    bottomLeft: Boolean = false,
    bottomRight: Boolean = false
) {
    shapeAppearanceModel =
        shapeAppearanceModel.toBuilder().apply {
            if(topLeft) setTopLeftCornerSize(resources.getDimensionPixelSize(R.dimen.corners_radius).toFloat())
            if(topRight) setTopRightCornerSize(resources.getDimensionPixelSize(R.dimen.corners_radius).toFloat())
            if(bottomLeft) setBottomLeftCornerSize(resources.getDimensionPixelSize(R.dimen.corners_radius).toFloat())
            if(bottomRight) setBottomRightCornerSize(resources.getDimensionPixelSize(R.dimen.corners_radius).toFloat())
        }.build()
}

inline fun View.snack(@StringRes messageRes: Int, length: Int = Snackbar.LENGTH_LONG, f: Snackbar.() -> Unit = {}) {
    snack(resources.getString(messageRes), length, f)
}

inline fun View.snack(message: String, length: Int = Snackbar.LENGTH_LONG, f: Snackbar.() -> Unit = {}) {
    val snack = Snackbar.make(this, message, length)
        .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
    val snackbarView: View = snack.view
    val textView = snackbarView.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
    textView.maxLines = 5

    snack.f()
    snack.show()
}