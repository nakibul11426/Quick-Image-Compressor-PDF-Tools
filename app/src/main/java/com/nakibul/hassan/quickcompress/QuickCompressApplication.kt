package com.nakibul.hassan.quickcompress

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class QuickCompressApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Timber for logging (always in debug builds)
        Timber.plant(Timber.DebugTree())
        
        Timber.d("QuickCompressApplication initialized")
    }
}
