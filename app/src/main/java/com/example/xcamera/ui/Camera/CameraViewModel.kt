package com.example.xcamera.ui.Camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.ui.geometry.Offset
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CameraViewModel : ViewModel() {

    private val _bitmaps = MutableStateFlow<List<Bitmap>>(emptyList())
    val bitmap = _bitmaps.asStateFlow()

    private var surfaceOrientedMeteringPointFactory : SurfaceOrientedMeteringPointFactory ? = null

    private var cameraSelector : CameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
    private var cameraControl : CameraControl? = null

    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    val surfaceRequest = _surfaceRequest.asStateFlow()

    private val cameraUseCaseBuilder = Preview.Builder().build().apply {
        setSurfaceProvider { surfaceRequest ->
            _surfaceRequest.update { surfaceRequest }
            surfaceOrientedMeteringPointFactory = SurfaceOrientedMeteringPointFactory(
                surfaceRequest.resolution.width.toFloat(),
                surfaceRequest.resolution.height.toFloat()
            )
        }
    }

    private val imageCaptureUseCase = ImageCapture.Builder().build()

    suspend fun bindToLifeCycle(
        lifecycleOwner: LifecycleOwner,
        context: Context
    ) {
        val processCameraProvider = ProcessCameraProvider.awaitInstance(context)
        processCameraProvider.unbindAll()
        val camera = processCameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            cameraUseCaseBuilder,
            imageCaptureUseCase
        )
        cameraControl = camera.cameraControl
        try {
            awaitCancellation()
        } finally {
            processCameraProvider.unbindAll()
            cameraControl = null
        }
    }

    fun switchCamera(lifecycleOwner: LifecycleOwner , context: Context) {
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA ) {
            CameraSelector.DEFAULT_BACK_CAMERA
        }else {
            CameraSelector.DEFAULT_FRONT_CAMERA
        }
        viewModelScope.launch {
            bindToLifeCycle(lifecycleOwner, context)
        }
    }

    fun tapToFocus( tapCoords : Offset) {
        val point  = surfaceOrientedMeteringPointFactory?.createPoint(tapCoords.x  , tapCoords.y)
        if(point != null) {
            val meteringAction = FocusMeteringAction.Builder(point)
                .build()

            cameraControl?.startFocusAndMetering(meteringAction)
        }
    }

    fun takePhoto(context: Context) {
        imageCaptureUseCase.takePicture(
            ContextCompat.getMainExecutor(context),
            object : OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)

                    val matrix = Matrix().apply {
                        postRotate((image.imageInfo.rotationDegrees.toFloat()))
                    }
                    val rotatedBitmap = Bitmap.createBitmap(
                        image.toBitmap(),
                        0,
                        0,
                        image.width,
                        image.height,
                        matrix,
                        true
                    )
                    _bitmaps.value += image.toBitmap()
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Log.e("Camera " ," Couldn't Take photo ", exception)
                }
            }
        )
    }

}