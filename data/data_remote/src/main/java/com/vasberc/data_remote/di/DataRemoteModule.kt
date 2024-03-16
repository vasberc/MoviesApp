package com.vasberc.data_remote.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.vasberc.data_remote.BuildConfig
import com.vasberc.data_remote.HeaderInterceptor
import com.vasberc.data_remote.service.MoviesService
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
@ComponentScan("com.vasberc.data_remote")
class DataRemoteModule

@Single
fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(okHttpClient)
        .build()
}

@Single
fun provideGson(): Gson {
    return GsonBuilder()
        .setLenient()
        .create()
}

@Single
fun provideOkHttpClient(context: Context, headerInterceptor: HeaderInterceptor): OkHttpClient {
    val cacheSize = (5 * 1024 * 1024).toLong()
    val cache = Cache(context.cacheDir, cacheSize)
    return OkHttpClient.Builder()
        .cache(cache)
        .addInterceptor(headerInterceptor)
        .connectTimeout(5500, TimeUnit.MILLISECONDS)
        .callTimeout(60, TimeUnit.SECONDS)
        .build()
}

@Single
fun provideMoviesService(retrofit: Retrofit): MoviesService {
    return retrofit.create(MoviesService::class.java)
}