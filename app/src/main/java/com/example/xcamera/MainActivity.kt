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
import com.example.xcamera.ui.Camera.BottomSheetPhotoContents
import com.example.xcamera.ui.Camera.CameraPreviewScreen
import com.example.xcamera.ui.Camera.CameraViewModel
import com.example.xcamera.ui.theme.XCameraTheme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            XCameraTheme {

                val viewmodel = viewModel<CameraViewModel>()
                val bitmaps by viewmodel.bitmap.collectAsStateWithLifecycle()
                val scaffoldState = rememberBottomSheetScaffoldState()
                BottomSheetScaffold(
                    scaffoldState = scaffoldState,
                    sheetContent = {
                        BottomSheetPhotoContents(
                            bitmaps = bitmaps,
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

