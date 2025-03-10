package com.example.xcamera.ui.mediaPlayer

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.ui.PlayerView

@Composable
fun PlayerScreen(contentUri : String , viewModel: PlayerViewModel = hiltViewModel()) {

    LaunchedEffect(contentUri) {
        viewModel.playVideo(Uri.parse(contentUri))
    }
    var lifecycle by remember {
        mutableStateOf(Lifecycle.Event.ON_CREATE)
    }
    val lifeCycleOwner = LocalLifecycleOwner.current
    DisposableEffect(
        lifeCycleOwner
    ) {
        val observer  = object  : LifecycleEventObserver {
            override fun onStateChanged(
                source: LifecycleOwner,
                event: Lifecycle.Event) {
                lifecycle = event
            }
        }
        lifeCycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(observer)
        }
    }
    AndroidView(
        factory = { context ->
            PlayerView(context).also {
                it.player = viewModel.player
            }
        },
        update = {
            when(lifecycle) {
                Lifecycle.Event.ON_PAUSE , Lifecycle.Event.ON_STOP -> {
                    it.onPause()
                    it.player?.pause()
                }
                Lifecycle.Event.ON_RESUME -> {
                    it.onResume()
                    it.player?.play()
                }
                Lifecycle.Event.ON_DESTROY -> {
                    it.player?.release()
                }
                else -> {}
            }

        }
    )
}