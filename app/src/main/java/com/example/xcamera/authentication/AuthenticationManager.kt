package com.example.xcamera.authentication

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class AuthenticationManager(
    private val activity : AppCompatActivity
) {
    private val _promptResult = Channel<BiometricResult>()
    val promptResult = _promptResult.receiveAsFlow()

    fun showBiometricPrompt(
         title : String,
         description : String,
         subtitle : String
    ) {
        val manager = BiometricManager.from(activity)

        val authenticators = if(Build.VERSION.SDK_INT >= 30) {
            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        } else {
            BiometricManager.Authenticators.BIOMETRIC_STRONG
        }

        val promptInfo = PromptInfo.Builder()
            .setTitle(title)
            .setDescription(description)
            .setSubtitle(subtitle)
            .setAllowedAuthenticators(authenticators)

        if (Build.VERSION.SDK_INT < 30) {
            promptInfo.setNegativeButtonText("Cancel !")
        }
        when(manager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                _promptResult.trySend(BiometricResult.AuthenticationNotSet)
                return
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                _promptResult.trySend(BiometricResult.HardwareUnavailable)
                return
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                _promptResult.trySend(BiometricResult.FeatureUnavailable)
                return
            }
            else -> Unit
        }
        val prompt = BiometricPrompt(
            activity,
            object : BiometricPrompt.AuthenticationCallback(){
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    _promptResult.trySend(BiometricResult.AuthenticationFailed)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    _promptResult.trySend(
                        BiometricResult.AuthenticationError(
                            errString.toString()
                        )
                    )
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    _promptResult.trySend(BiometricResult.AuthenticationSuccess)
                }
            }
        )
        prompt.authenticate(promptInfo.build())

    }

    sealed interface BiometricResult {
        data object HardwareUnavailable : BiometricResult
        data object FeatureUnavailable : BiometricResult
        data class AuthenticationError(val errorString : String) : BiometricResult
        data object AuthenticationFailed : BiometricResult
        data object AuthenticationSuccess : BiometricResult
        data object AuthenticationNotSet : BiometricResult
    }
}