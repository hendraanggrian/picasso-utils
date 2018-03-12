package com.hendraanggrian.pikasso

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout.LayoutParams
import android.widget.ImageView
import android.widget.ProgressBar
import com.hendraanggrian.pikasso.target.PlaceholderTarget
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import com.squareup.picasso.Target
import java.lang.Exception

/** Sets circular progress bar with defined width and height. */
fun ImageView.toProgressTarget(size: Int = WRAP_CONTENT): Target {
    val progressBar = ProgressBar(context)
    progressBar.layoutParams = LayoutParams(size, size).apply { gravity = Gravity.CENTER }
    return PlaceholderTarget(this, progressBar)
}

/** Sets horizontal progress with defined gravity. */
fun ImageView.toHorizontalProgressTarget(gravity: Int = Gravity.BOTTOM): Target {
    val progressBar = ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal)
    progressBar.layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
        this.gravity = gravity
    }
    progressBar.isIndeterminate = true
    return PlaceholderTarget(this, progressBar)
}

/** Set custom view as target's placeholder. */
@Suppress("NOTHING_TO_INLINE")
inline fun ImageView.toTarget(placeholder: View): Target =
    PlaceholderTarget(this, placeholder)

/**
 * Completes the request into a [Target] with Kotlin DSL, returning the [Target] created.
 *
 * @see RequestCreator.into
 */
inline fun RequestCreator.into(target: TargetBuilder.() -> Unit): Target =
    _Target().apply(target).also { into(it) }

/** Interface to create [Target] with Kotlin DSL. */
interface TargetBuilder {

    /** Invoked when image is successfully loaded. */
    fun onLoaded(callback: (Bitmap, from: Picasso.LoadedFrom) -> Unit)

    /** Invoked when image failed to load. */
    fun onFailed(callback: (e: Exception, Drawable?) -> Unit)

    /** Invoked when image has started loading. */
    fun onPrepare(callback: (Drawable?) -> Unit)
}

@PublishedApi
internal class _Target : Target, TargetBuilder {
    private var _onLoaded: ((Bitmap, Picasso.LoadedFrom) -> Unit)? = null
    private var _onFailed: ((Exception, Drawable?) -> Unit)? = null
    private var _onPrepare: ((Drawable?) -> Unit)? = null

    override fun onLoaded(callback: (Bitmap, from: Picasso.LoadedFrom) -> Unit) {
        _onLoaded = callback
    }

    override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
        _onLoaded?.invoke(bitmap, from)
    }

    override fun onFailed(callback: (Exception, Drawable?) -> Unit) {
        _onFailed = callback
    }

    override fun onBitmapFailed(e: Exception, errorDrawable: Drawable?) {
        _onFailed?.invoke(e, errorDrawable)
    }

    override fun onPrepare(callback: (Drawable?) -> Unit) {
        _onPrepare = callback
    }

    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
        _onPrepare?.invoke(placeHolderDrawable)
    }
}