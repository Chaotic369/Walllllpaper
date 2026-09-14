package com.yourdomain.webwallpaper

import android.app.Application
import com.yourdomain.webwallpaper.data.AppDatabase
import com.yourdomain.webwallpaper.data.PreferencesManager

class WallpaperApplication : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }
    val preferencesManager by lazy { PreferencesManager(this) }
    
}
