package com.yourdomain.webwallpaper.service

import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder
import com.yourdomain.webwallpaper.WallpaperApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class LiveWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return WebEngine()
    }

    inner class WebEngine : Engine() {
        private var renderer: WallpaperRenderer? = null
        private val engineScope = CoroutineScope(Dispatchers.Main + Job())
        private var currentUrl: String = ""
        private var currentMode: String = ""

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            
            // Enable touch events for the HTML touch pass-through
            setTouchEventsEnabled(true)

            val prefs = (application as WallpaperApplication).preferencesManager
            
            // Listen for changes in preferences to update the wallpaper live
            engineScope.launch {
                combine(prefs.activeModeFlow, prefs.sourceUrlFlow) { mode, url ->
                    Pair(mode, url)
                }.collect { (mode, url) ->
                    if (mode != currentMode || url != currentUrl) {
                        currentMode = mode
                        currentUrl = url
                        switchRenderer(mode, url, surfaceHolder)
                    }
                }
            }
        }

        private fun switchRenderer(mode: String, url: String, surfaceHolder: SurfaceHolder) {
            renderer?.onDestroy() // Clean up old renderer to prevent memory leaks
            
            if (url.isEmpty()) return

            renderer = if (mode == "video") {
                VideoRenderer(this@LiveWallpaperService, url)
            } else {
                WebViewRenderer(this@LiveWallpaperService, url)
            }
            
            renderer?.onCreate(surfaceHolder)
            
            // If the surface is already created and sized, trigger a size update immediately
            val surfaceFrame = surfaceHolder.surfaceFrame
            if (surfaceFrame.width() > 0 && surfaceFrame.height() > 0) {
                renderer?.onSurfaceChanged(surfaceFrame.width(), surfaceFrame.height())
            }
            
            // Trigger visibility state so it plays if the home screen is currently visible
            renderer?.onVisibilityChanged(isVisible)
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            renderer?.onSurfaceChanged(width, height)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            renderer?.onVisibilityChanged(visible)
        }

        override fun onTouchEvent(event: MotionEvent) {
            super.onTouchEvent(event)
            renderer?.onTouchEvent(event)
        }

        override fun onDestroy() {
            super.onDestroy()
            engineScope.cancel()
            renderer?.onDestroy()
        }
    }
}
