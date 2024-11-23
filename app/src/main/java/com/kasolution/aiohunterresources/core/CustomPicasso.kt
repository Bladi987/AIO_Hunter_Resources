package com.kasolution.aiohunterresources.core

import android.content.Context
import com.squareup.picasso.LruCache
import com.squareup.picasso.Picasso

object CustomPicasso {

    private var instance: Picasso? = null

    fun getInstance(context: Context): Picasso {
        if (instance == null) {
            instance = Picasso.Builder(context)
                .memoryCache(createLruCache())
                .build()
        }
        return instance!!
    }

    private fun createLruCache(): LruCache {
        // Configurar una caché en memoria con un tamaño de 10 MB
        val maxMemory = Runtime.getRuntime().maxMemory().toInt()
        val cacheSize = maxMemory / 8
        return LruCache(cacheSize)
    }
}
