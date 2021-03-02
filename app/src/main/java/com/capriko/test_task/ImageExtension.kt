package com.capriko.test_task

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey

fun ImageView.loadImage(url: String, forceCache: Boolean, callback: RequestListener<Drawable>) {
    Glide.with(this.context).load(url).apply {
        onlyRetrieveFromCache(forceCache)
        skipMemoryCache(true)
        error(R.drawable.placeholder)
        diskCacheStrategy(DiskCacheStrategy.ALL)
        listener(callback)
        into(this@loadImage)
    }

}