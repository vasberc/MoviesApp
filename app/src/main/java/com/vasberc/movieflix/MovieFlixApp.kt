package com.vasberc.movieflix

import android.app.Application
import com.vasberc.data.di.DataModule
import com.vasberc.data_local.daos.MovieRemoteKeysDao
import com.vasberc.data_local.daos.MoviesDao
import com.vasberc.data_local.di.DataLocalModule
import com.vasberc.data_remote.di.DataRemoteModule
import com.vasberc.presentation.di.PresentationModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module
import timber.log.Timber

class MovieFlixApp: Application() {

    override fun onCreate() {
        super.onCreate()

        startTimber()

        startKoin()

        clearDataForAppStart()

    }

    private fun clearDataForAppStart() {
        CoroutineScope(Dispatchers.IO).launch {
            val moviesDao: MoviesDao by inject()
            moviesDao.clearAllEntities()
            val remoteKeysDao: MovieRemoteKeysDao by inject()
            remoteKeysDao.clearRemoteKeys()
            Timber.d("Hellooooooooo Data cleared")
        }
    }

    private fun startTimber() {
        if(BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun startKoin() {
        startKoin {
            androidContext(this@MovieFlixApp)
            modules(PresentationModule().module, DataModule().module, DataRemoteModule().module, DataLocalModule().module)
        }
    }
}