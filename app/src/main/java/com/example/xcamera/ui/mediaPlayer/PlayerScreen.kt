package com.example.xcamera.ui.mediaPlayer

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.ui.PlayerView

@Composable
fun PlayerScreen(videoPath: String) {

    val viewModel : PlayerViewModel = hiltViewModel()
    val player = viewModel.getPlayer()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    val videoUri = remember {
        Uri.parse(videoPath)
    }
    LaunchedEffect(videoUri) {
        viewModel.initializePlayer(videoUri)
    }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver{ _ , event ->
            when(event) {
                Lifecycle.Event.ON_PAUSE -> viewModel.savePlayerState()
                Lifecycle.Event.ON_RESUME -> viewModel.initializePlayer(videoUri)
                Lifecycle.Event.ON_DESTROY -> viewModel.savePlayerState()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
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