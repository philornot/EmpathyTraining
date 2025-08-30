package com.empathytraining

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Application class for the Empathy Training app This class is required
 * for Hilt dependency injection
 */
@HiltAndroidApp
class EmpathyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Timber for logging
        // In debug builds, plant a debug tree
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        Timber.d("EmpathyApplication started")
    }
}