package com.yourdomain.webwallpaper.service

import android.view.MotionEvent
import android.view.SurfaceHolder

interface WallpaperRenderer {
    /** Called when the engine is first created and the surface is available. */
    fun onCreate(surfaceHolder: SurfaceHolder)
    
    /** Called when the surface size changes (e.g., device rotation). */
    fun onSurfaceChanged(width: Int, height: Int)
    
    /** Called when the wallpaper is no longer visible (e.g., app opened). Use this to pause rendering. */
    fun onVisibilityChanged(visible: Boolean)
    
    /** Passes touch events from the home screen into the renderer. */
    fun onTouchEvent(event: MotionEvent)
    
    /** Called when the surface is destroyed. Clean up players and webviews here. */
    fun onDestroy()
}
