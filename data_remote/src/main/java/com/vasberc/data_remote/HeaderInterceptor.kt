package com.vasberc.data_remote

import okhttp3.Interceptor
import okhttp3.Response
import org.koin.core.annotation.Single

@Single
class HeaderInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val chainRequestBuilder = chain.request().newBuilder()
        chainRequestBuilder.addHeader("accept", "application/json")
        chainRequestBuilder.addHeader("Authorization", "Bearer ${BuildConfig.ACCESS_TOKEN}")
        return chain.proceed(chainRequestBuilder.build())
    }
}