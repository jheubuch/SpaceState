package de.hbch.spacestate.shared

import de.hbch.spacestate.BuildConfig
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.Request

fun Request.Builder.prepareRequest(url: String): Request.Builder {
    return this.get()
        .addHeader("Accept", "application/json")
        .addHeader("User-Agent", "SpaceState ${BuildConfig.APPLICATION_ID}/${BuildConfig.VERSION_NAME}")
        .url(url)
}

val NetworkClient: OkHttpClient = OkHttpClient.Builder().dispatcher(Dispatcher().also { it.maxRequests = 5 }).build()
