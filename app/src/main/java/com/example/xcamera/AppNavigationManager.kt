package com.example.xcamera

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.xcamera.ui.camera.CameraScaffoldScreen
import com.example.xcamera.ui.mediaPlayer.PlayerScreen
import kotlinx.serialization.Serializable


@Composable
fun AppNavigationManager(modifier: Modifier = Modifier) {

    val navController = rememberNavController()
    Log.d("nav" , "nav opened")
    NavHost(
        navController = navController,
        startDestination = CameraScreen1
    ) {
        composable<CameraScreen1>{
            CameraScaffoldScreen( navController)
        }
        composable<VideoPlayer> {
            val args = it.toRoute<VideoPlayer>()
            PlayerScreen(
                contentUri = args.path
            )
        }
    }
}

@Serializable
object CameraScreen1

@Serializable
data class VideoPlayer(
    val path : String
)