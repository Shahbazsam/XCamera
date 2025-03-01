@file:OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)

package com.example.xcamera.ui.camera

import android.content.Context
import android.util.Log
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.CameraSelector
import androidx.camera.viewfinder.compose.MutableCoordinateTransformer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.geometry.takeOrElse
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID


@Composable
fun CameraScaffoldScreen(navController: NavController) {
    Log.d("nav" , "nav after")
    val viewmodel : CameraViewModel = hiltViewModel()
    val photos by viewmodel.photoUiState.collectAsStateWithLifecycle()
    val videos by viewmodel.videoUiState.collectAsStateWithLifecycle()
    val scaffoldState = rememberBottomSheetScaffoldState()
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            BottomSheetPhotoContents(
                navController = navController,
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


@Composable
fun CameraPreviewScreen(
    viewModel: CameraViewModel,
    innerPadding: PaddingValues,
    scaffoldState: BottomSheetScaffoldState
) {

    val cameraPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO
        )
    )
    if (cameraPermissionState.allPermissionsGranted) {
        CameraPreviewContent(
            viewModel,
            innerPadding,
            scaffoldState
        )
    } else {
        CameraPermissionContent(innerPadding, cameraPermissionState)
    }
}

@Composable
fun CameraPermissionContent(
    innerPadding: PaddingValues,
    cameraPermissionState: MultiplePermissionsState
) {
    Column (
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxHeight()
            .background(color = Color.DarkGray),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        val text = if (cameraPermissionState.shouldShowRationale) {
            "Whoops! Looks like we need your camera to work our magic!" +
                    "Don't worry, we just wanna see your pretty face (and maybe some cats).  " +
                    "Grant us permission and let's get this party started!"
        } else {
            "Hi there! We need your camera to work our magic! ✨\n" +
                    "Grant us permission and let's get this party started! \uD83C\uDF89"
        }
        Text(
            text,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(22.dp))
        Button(
            onClick = {
                cameraPermissionState.launchMultiplePermissionRequest()
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Grant Permissions",
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CameraPreviewContent(
    viewModel: CameraViewModel,
    innerPadding: PaddingValues,
    scaffoldState: BottomSheetScaffoldState,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    context: Context = LocalContext.current
    ) {
    //states for quality and mode
    val quality by viewModel.qualitySelector.collectAsStateWithLifecycle()
    var qualitySelector by remember { mutableStateOf(false) }
    var isVideoMode by remember { mutableStateOf(false) }

    // Video recording state
    var isRecording by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var recordingTime by remember { mutableStateOf(0) }

    // Surface request state
    val surfaceRequest = viewModel.surfaceRequest.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    // Auto Focus state
    var autoFocusRequest by remember { mutableStateOf(UUID.randomUUID() to Offset.Unspecified)  }
    val autoFocusRequestId = autoFocusRequest.first
    val showAutoFocusIndicator = autoFocusRequest.second.isSpecified
    val autoFocusCoords = remember(autoFocusRequestId) {autoFocusRequest.second }

    // Zooming Variables
    val zoomState  by viewModel.zoomState.collectAsStateWithLifecycle()
    val currentZoom by viewModel.currentZoom.collectAsStateWithLifecycle()
    val(minZoom , maxZoom) = zoomState

    // current camera
    val currentCamera = viewModel.cameraSelectorInfo.collectAsStateWithLifecycle()

    LaunchedEffect(isRecording && !isPaused) {
        while (isRecording){
            delay(1000)
            recordingTime++
        }
    }

    if (showAutoFocusIndicator) {
        LaunchedEffect(autoFocusRequestId) {
            delay(1000)
            autoFocusRequest = autoFocusRequestId to Offset.Unspecified
        }
    }

    LaunchedEffect(lifecycleOwner) {
        viewModel.bindToLifeCycle(
            lifecycleOwner, context
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        surfaceRequest.value?.let { request ->
            val coordinateTransformer = remember { MutableCoordinateTransformer() }
            CameraXViewfinder(
                surfaceRequest = request,
                coordinateTransformer = coordinateTransformer,
                modifier = Modifier.pointerInput(Unit) {
                    detectTapGestures { tapCoords ->
                        with(coordinateTransformer) {
                            viewModel.tapToFocus(tapCoords.transform())
                        }
                        autoFocusRequest = UUID.randomUUID() to tapCoords
                    }
                }
            )
            AnimatedVisibility(
                visible = showAutoFocusIndicator,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .offset { autoFocusCoords.takeOrElse { Offset.Zero }.round() }
                    .offset((-24).dp, (-24).dp)
            ) {
                Spacer(Modifier
                    .border(2.dp, Color.White, CircleShape)
                    .size(48.dp))
            }
        }
        Box (
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
        ){
            CameraQualitySelector(
                quality = quality,
                qualitySelector = qualitySelector,
                onQualitySelectorChange = {qualitySelector = !qualitySelector} ,
                onQualitySelected = { selectedQuality ->
                    viewModel.setVideoQuality(lifecycleOwner , selectedQuality  , context)
                }
            )
        }
        Column (
            modifier = Modifier
                .align(Alignment.BottomCenter),
        ){
            if (currentCamera.value == CameraSelector.DEFAULT_BACK_CAMERA) {
                Slider(
                    value = viewModel.zoomRatioToLinearZoom(currentZoom),
                    valueRange = 0f..1f,
                    onValueChange = { linearZoom ->
                        viewModel.setLinearZoom(linearZoom)
                    },
                    modifier = Modifier.padding(16.dp)
                )
                Text(
                    text = "Zoom: ${String.format("%.1fx" , currentZoom)}",
                    modifier = Modifier
                        .padding(16.dp),
                    color = Color.White
                )
                Row(modifier = Modifier.fillMaxWidth() , horizontalArrangement = Arrangement.SpaceEvenly) {
                    if (minZoom < 1.0f){
                        Button(onClick = { viewModel.setZoomRatio(minZoom) }) { Text("${minZoom}x") }
                        Button(onClick = { viewModel.setZoomRatio(1.0f) }) { Text("1x") }
                    }else {
                        Button(onClick = { viewModel.setZoomRatio(1.0f) }) { Text("1x") }
                    }
                    Button(onClick = { viewModel.setZoomRatio(3.0f) }) { Text("3x") }
                    Button(onClick = { viewModel.setZoomRatio(5.0f) }) { Text("5x") }
                    Button(onClick = { viewModel.setZoomRatio(maxZoom) }) { Text("${maxZoom}x") }
                }
            }
            Divider(
                color = Color.White,
                thickness = 2.dp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(18.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 140.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    modifier = Modifier
                        .clickable {
                            isVideoMode = false
                        },
                    text = "photo",
                    color = if (!isVideoMode) Color.White else Color.Gray
                )
                Text(
                    modifier = Modifier
                        .clickable {
                            isVideoMode = true
                        },
                    text = "video",
                    color = if (isVideoMode) Color.White else Color.Gray
                )
            }
        }
        if (!isVideoMode) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 54.dp)
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PhotoControls(
                    viewModel = viewModel,
                    scaffoldState = scaffoldState,
                    scope = scope,
                    context = context
                )
            }
        }else {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 54.dp)
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                VideoControls(
                    viewModel = viewModel,
                    scaffoldState = scaffoldState,
                    scope = scope,
                    isRecording = isRecording,
                    isPaused = isPaused,
                    recordingTime = recordingTime,
                    onRecordingStateChange = { recording, paused, time ->
                        isRecording = recording
                        isPaused = paused
                        recordingTime = time
                    },
                    lifecycleOwner = lifecycleOwner,
                    context = context
                )
            }
        }
    }
}

@Composable
fun PhotoControls(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    viewModel: CameraViewModel,
    scaffoldState: BottomSheetScaffoldState,
    scope: CoroutineScope,
    context: Context,
    ) {
    IconButton(
        onClick = {
            scope.launch {
                scaffoldState.bottomSheetState.expand()
            }
        }
    ) {
        Icon(
            imageVector = Icons.Default.Photo,
            contentDescription = "Open Gallery "
        )
    }
    IconButton(
        onClick = {
            viewModel.takePhoto(context)
        }
    ) {
        Icon(
            imageVector = Icons.Default.Camera,
            contentDescription = "Take Photo"
        )
    }
    IconButton(
        onClick = {
            viewModel.switchCamera(lifecycleOwner, context)
        }
    ) {
        Icon(
            imageVector = Icons.Default.Cameraswitch,
            contentDescription = "Switch Camera"
        )
    }
}

@Composable
fun VideoControls(
    viewModel: CameraViewModel,
    scaffoldState: BottomSheetScaffoldState,
    scope: CoroutineScope,
    isRecording: Boolean,
    isPaused: Boolean,
    recordingTime: Int,
    onRecordingStateChange: (Boolean, Boolean, Int) -> Unit,
    lifecycleOwner: LifecycleOwner,
    context: Context
) {
    IconButton(
        onClick = {
            scope.launch {
                scaffoldState.bottomSheetState.expand()
            }
        }
    ) {
        Icon(
            imageVector = Icons.Default.Photo,
            contentDescription = "Open Gallery "
        )
    }
    if ( isRecording  && !isPaused ) {
        IconButton(
            onClick = {
                viewModel.pauseRecording()
                onRecordingStateChange(false , true , recordingTime)
            }
        ) {
            Icon(
                imageVector = Icons.Default.Pause,
                contentDescription = "pause Video"
            )
        }
        Text(
            text = "$recordingTime Sec",
        )
        IconButton(
            onClick = {
                viewModel.stopRecording()
                onRecordingStateChange(false,false, 0)
            }
        ) {
            Icon(
                imageVector = Icons.Default.Stop,
                contentDescription = "stop video"
            )
        }
    } else if(!isRecording && isPaused) {
        IconButton(
            onClick = {
                viewModel.resumeRecording()
                onRecordingStateChange(true, false, recordingTime)
            }
        ) {
            Icon(
                imageVector = Icons.Default.Repeat,
                contentDescription = "resume"
            )
        }
        IconButton(
            onClick = {
                viewModel.stopRecording()
                onRecordingStateChange(false, false, 0)
            }
        ) {
            Icon(
                imageVector = Icons.Default.Stop,
                contentDescription = "stop video"
            )
        }
    }else {
        IconButton(
            onClick = {
                viewModel.startRecording(context)
                onRecordingStateChange(true, false, 0)
            }
        ) {
            Icon(
                imageVector = Icons.Default.Videocam,
                contentDescription = "Take Photo"
            )
        }
    }
    IconButton(
        onClick = {
            viewModel.switchCamera(lifecycleOwner, context)
        }
    ) {
        Icon(
            imageVector = Icons.Default.Cameraswitch,
            contentDescription = "Switch Camera"
        )
    }
}