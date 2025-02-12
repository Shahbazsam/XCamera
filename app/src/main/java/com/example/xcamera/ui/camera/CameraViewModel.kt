package com.example.xcamera.ui.camera

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
import com.example.xcamera.data.local.Photos
import com.example.xcamera.data.repository.PhotoRepository
import com.example.xcamera.ui.state.PhotoState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val photoRepository: PhotoRepository
) : ViewModel() {

    val photoUiState : StateFlow<List<PhotoState>> = photoRepository.getAllPhotos()
        .map { photos ->
            photos.map {
                PhotoState(
                    it.id , it.photoPath
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily , emptyList())

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
                    viewModelScope.launch {
                        val path = savePhotoPrivately(context , rotatedBitmap)
                        photoRepository.insertPhoto(
                            Photos(
                                photoPath = path
                            )
                        )
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Log.e("Camera " ," Couldn't Take photo ", exception)
                }
            }
        )
    }

    private fun savePhotoPrivately(context: Context , bitmap : Bitmap) : String {
        val directory = File(context.filesDir , "PrivateGallery")
        if (!directory.exists()) directory.mkdirs()

        val file  = File(directory , "photo_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG , 100 , outputStream)
        outputStream.flush()
        outputStream.close()
        return file.absolutePath
    }

    fun deletePhoto(photo : Photos) {
        viewModelScope.launch {
            val file = File(photo.photoPath)
            if (file.exists()) {
                file.delete()
            }
            photoRepository.deletePhoto( photo )
        }
    }

    fun getPhotoById(id: Int) {
        viewModelScope.launch {
            photoRepository.getPhotoById(
                id
            )
        }
    }

}