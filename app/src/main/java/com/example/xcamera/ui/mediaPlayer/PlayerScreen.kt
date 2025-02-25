package com.example.xcamera.ui.mediaPlayer

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.ui.PlayerView

@Composable
fun PlayerScreen(videoPath: String) {

    val viewModel = viewModel<PlayerViewModel>()
    val player = viewModel.getPlayer()
    val context = LocalContext.current

    val videoUri = remember {
        Uri.parse(videoPath)
    }
    LaunchedEffect(Unit) {
        viewModel.initializePlayer(videoUri)
    }
    DisposableEffect(Unit) {
        onDispose {
            viewModel.getPlayer()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PlayerView(ctx).apply {
                    this.player = player
                }
            }
        )

    }
}