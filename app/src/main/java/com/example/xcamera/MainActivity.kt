package com.example.xcamera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.xcamera.ui.camera.BottomSheetPhotoContents
import com.example.xcamera.ui.camera.CameraPreviewScreen
import com.example.xcamera.ui.camera.CameraViewModel
import com.example.xcamera.ui.theme.XCameraTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            XCameraTheme {

                val viewmodel = viewModel<CameraViewModel>()
                val photos by viewmodel.photoUiState.collectAsStateWithLifecycle()
                val videos by viewmodel.videoUiState.collectAsStateWithLifecycle()
                val scaffoldState = rememberBottomSheetScaffoldState()
                BottomSheetScaffold(
                    scaffoldState = scaffoldState,
                    sheetContent = {
                        BottomSheetPhotoContents(
                            photos = photos,
                            videos = videos,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    },
                    sheetPeekHeight = 0.dp
                ) {  innerPadding ->

                        CameraPreviewScreen(
                            viewmodel,
                            innerPadding,
                            scaffoldState,
                        )
                }
            }
        }
    }
}

