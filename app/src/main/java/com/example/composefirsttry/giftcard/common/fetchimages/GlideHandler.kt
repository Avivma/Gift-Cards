package com.example.composefirsttry.giftcard.common.fetchimages

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GlideHandler @Inject constructor(
    private val context: Context,
) {
    suspend fun downloadImage(imageUrl: String) {
        withContext(Dispatchers.IO) {
            Glide.with(context)
                .load(imageUrl)
                .preload()
            delay(3000)
        }
    }

/*    suspend fun downloadImage(imageUrl: String) = suspendCoroutine<Boolean> { continuation ->
        Glide.with(context)
            .load(imageUrl)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(@Nullable e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                    continuation.resumeWithException(e ?: Exception("Unknown GlideException"))
                    return true
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    GlobalScope.launch(Dispatchers.IO) {
                        delay(3000)
                        continuation.resume(true)
                    }
                    return true
                }
            })
            .preload()
    }*/

    companion object {
        @JvmStatic
        fun loadImage(imageView: ImageView, url: String) {
            Glide.with(imageView.context)
                .load(url)
                .into(imageView)
        }
    }
}