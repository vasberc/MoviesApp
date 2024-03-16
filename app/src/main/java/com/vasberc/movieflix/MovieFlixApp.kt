package com.vasberc.movieflix

import android.app.Application
import com.vasberc.data.di.DataModule
import com.vasberc.presentation.di.PresentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

class MovieFlixApp: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MovieFlixApp)
            modules(PresentationModule().module, DataModule().module)
        }
    }
}