package com.yourdomain.webwallpaper.service

import android.content.Context
import android.net.Uri
import android.view.MotionEvent
import android.view.SurfaceHolder
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class VideoRenderer(
    private val context: Context,
    private val videoUri: String
) : WallpaperRenderer {

    private var exoPlayer: ExoPlayer? = null
    private var surfaceHolder: SurfaceHolder? = null
    private var isVisible: Boolean = false

    override fun onCreate(surfaceHolder: SurfaceHolder) {
        this.surfaceHolder = surfaceHolder
        exoPlayer = ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ALL
            volume = 0f // Live wallpapers should be muted
            
            val mediaItem = MediaItem.fromUri(Uri.parse(videoUri))
            setMediaItem(mediaItem)
            
            // Bind the player directly to the live wallpaper surface
            setVideoSurfaceHolder(surfaceHolder)
            
            prepare()
        }
    }

    override fun onSurfaceChanged(width: Int, height: Int) {
        // ExoPlayer automatically handles surface scaling (Fit/Crop) based on 
        // the parameters set by the parent LiveWallpaperService.
    }

    override fun onVisibilityChanged(visible: Boolean) {
        this.isVisible = visible
        if (visible) {
            exoPlayer?.play()
        } else {
            exoPlayer?.pause()
        }
    }

    override fun onTouchEvent(event: MotionEvent) {
        // Video backgrounds typically ignore touches, but you can add tap-to-pause 
        // or ripple effects here if desired.
    }

    override fun onDestroy() {
        exoPlayer?.release()
        exoPlayer = null
    }
}
