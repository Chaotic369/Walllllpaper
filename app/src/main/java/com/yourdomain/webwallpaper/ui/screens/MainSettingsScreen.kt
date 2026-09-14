package com.yourdomain.webwallpaper.ui.screens

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourdomain.webwallpaper.WallpaperApplication
import com.yourdomain.webwallpaper.service.LiveWallpaperService
import kotlinx.coroutines.launch

@Composable
fun MainSettingsScreen() {
    val context = LocalContext.current
    val app = context.applicationContext as WallpaperApplication
    val prefs = app.preferencesManager
    val coroutineScope = rememberCoroutineScope()

    // State Variables
    var activeMode by remember { mutableStateOf("web") }
    var sourceUrl by remember { mutableStateOf("") }
    var zoomSize by remember { mutableStateOf("1.0") }
    var scaleMode by remember { mutableStateOf("Center Crop") }
    var showHistory by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Header (History only, aligned right)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "History",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { showHistory = true }
                )
            }

            // Live Preview (Portrait, 9/16 aspect ratio)
            Box(
                modifier = Modifier
                    .width(140.dp)
                    .aspectRatio(9f / 16f)
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black)
                    .border(1.dp, MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                    .padding(bottom = 16.dp), // spacing below preview
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "LIVE PREVIEW",
                    color = Color.White,
                    fontSize = 11.sp,
                    modifier = Modifier
                        .background(Color(0x99000000), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Segmented Control (Web / Video / HTML)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                SegmentButton("Web", activeMode == "web") { activeMode = "web" }
                SegmentButton("Video", activeMode == "video") { activeMode = "video" }
                SegmentButton("HTML", activeMode == "html") { activeMode = "html" }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Source URL Input
            InputLabel("SOURCE URL")
            CustomTextField(
                value = sourceUrl,
                onValueChange = { sourceUrl = it },
                placeholder = "https://example.com"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Zoom Size Input
            InputLabel("ZOOM SIZE")
            CustomTextField(
                value = zoomSize,
                onValueChange = { zoomSize = it },
                placeholder = "1.0"
            )

            // Scale Mode (Only visible if Video is selected)
            if (activeMode == "video") {
                Spacer(modifier = Modifier.height(16.dp))
                InputLabel("SCALE MODE")
                // Simplified static display for MVP. In full app, use DropdownMenu here.
                CustomTextField(
                    value = scaleMode,
                    onValueChange = { scaleMode = it },
                    placeholder = "Center Crop"
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Apply Button
            Button(
                onClick = {
                    coroutineScope.launch {
                        // Save inputs to DataStore
                        val zoomFloat = zoomSize.toFloatOrNull() ?: 1.0f
                        prefs.saveSettings(activeMode, sourceUrl, zoomFloat, scaleMode)
                        
                        // Launch Android Live Wallpaper Picker
                        val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
                            putExtra(
                                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                                ComponentName(context, LiveWallpaperService::class.java)
                            )
                        }
                        context.startActivity(intent)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Set Wallpaper", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        }

        // History Overlay UI Injection
        if (showHistory) {
            HistoryOverlay(onClose = { showHistory = false })
        }
    }
}

@Composable
fun InputLabel(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.secondary,
        fontSize = 11.sp,
        letterSpacing = 0.5.sp
    )
    Spacer(modifier = Modifier.height(6.dp))
}

@Composable
fun CustomTextField(value: String, onValueChange: (String) -> Unit, placeholder: String) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.secondary) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground
        ),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun RowScope.SegmentButton(text: String, active: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (active) MaterialTheme.colorScheme.background else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (active) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}
