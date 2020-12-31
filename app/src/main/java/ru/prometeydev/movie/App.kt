package ru.prometeydev.movie

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.prometeydev.movie.di.networkModule
import ru.prometeydev.movie.di.repoModule
import ru.prometeydev.movie.di.viewModelModule

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(listOf(networkModule, repoModule, viewModelModule))
        }
    }

}