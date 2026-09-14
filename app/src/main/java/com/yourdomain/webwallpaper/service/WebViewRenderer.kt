package com.yourdomain.webwallpaper.service

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient

class WebViewRenderer(
    private val context: Context,
    private val url: String
) : WallpaperRenderer {

    private var webView: WebView? = null
    private var surfaceHolder: SurfaceHolder? = null
    private var width: Int = 0
    private var height: Int = 0
    private var isVisible: Boolean = false
    private val mainHandler = Handler(Looper.getMainLooper())

    // 60 FPS Render Loop for CSS Animations/Clocks
    private val renderRunnable = object : Runnable {
        override fun run() {
            if (isVisible) {
                draw()
                mainHandler.postDelayed(this, 16)
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(surfaceHolder: SurfaceHolder) {
        this.surfaceHolder = surfaceHolder
        webView = WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.mediaPlaybackRequiresUserGesture = false
            settings.cacheMode = WebSettings.LOAD_DEFAULT
            webViewClient = WebViewClient()
            setBackgroundColor(0x000000) // Default black background
        }
        
        webView?.loadUrl(url)
    }

    override fun onSurfaceChanged(width: Int, height: Int) {
        this.width = width
        this.height = height
        layoutWebView()
    }

    override fun onVisibilityChanged(visible: Boolean) {
        this.isVisible = visible
        if (visible) {
            webView?.onResume()
            mainHandler.post(renderRunnable) // Start the engine
        } else {
            webView?.onPause()
            mainHandler.removeCallbacks(renderRunnable) // Battery saver
        }
    }

    override fun onTouchEvent(event: MotionEvent) {
        // TOUCH PASS-THROUGH: Forwards raw touch data directly to the HTML
        webView?.dispatchTouchEvent(event)
    }

    override fun onDestroy() {
        mainHandler.removeCallbacks(renderRunnable)
        webView?.destroy()
        webView = null
    }

    private fun layoutWebView() {
        webView?.let {
            it.measure(
                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
            )
            it.layout(0, 0, width, height)
        }
    }

    private fun draw() {
        if (width == 0 || height == 0) return

        var canvas: Canvas? = null
        try {
            canvas = surfaceHolder?.lockCanvas()
            canvas?.let {
                webView?.draw(it)
            }
        } finally {
            if (canvas != null) {
                surfaceHolder?.unlockCanvasAndPost(canvas)
            }
        }
    }
}
