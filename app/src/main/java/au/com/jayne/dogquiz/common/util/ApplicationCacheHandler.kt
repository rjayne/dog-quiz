package au.com.jayne.dogquiz.common.util

import android.content.Context
import android.net.http.HttpResponseCache
import timber.log.Timber

import java.io.File
import java.io.IOException

object ApplicationCacheHandler {

    fun initializeApplication(context: Context) {
        setupHttpCache(context)
    }

    private fun setupHttpCache(context: Context) {
        val cache = HttpResponseCache.getInstalled()
        if (cache == null) {
            installHttpCache(context)
        }
    }

    // Enabling caching of all of the application's HTTP requests
    private fun installHttpCache(context: Context) {
        Timber.d("installing HttpCache")
        try {
            val httpCacheDir = File(context.cacheDir, "https")
            val httpCacheSize = (10 * 1024 * 1024).toLong() // 10 MiB
            HttpResponseCache.install(httpCacheDir, httpCacheSize)
        } catch (e: IOException) {
            Timber.i("HTTP response cache installation failed:$e")
        }
    }

}
