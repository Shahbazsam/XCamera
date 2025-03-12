package com.example.xcamera.authentication

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager.*
import androidx.biometric.BiometricManager.Authenticators.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.xcamera.MainActivity
import com.example.xcamera.authentication.AuthenticationManager.*

@Composable
fun AuthenticationScreen(
    manager: AuthenticationManager,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val promptResult by manager.promptResult.collectAsState(null)

    val enrollLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
        }
    )
    LaunchedEffect(promptResult) {
        if (promptResult is BiometricResult.AuthenticationNotSet) {
            if(Build.VERSION.SDK_INT >= 30) {
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                    )
                }
                enrollLauncher.launch(enrollIntent)
            }
        }else if (promptResult is BiometricResult.AuthenticationSuccess) {
           val intent =  Intent(context , MainActivity::class.java)
            context.startActivity(intent)
            (context as? Activity)?.finish()
        }
    }
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                manager.showBiometricPrompt(
                    title = "Authenticate to Continue",
                    description = "You can use your PIN , Pattern or Password",
                    subtitle = "Use Biometric or Device Credential"
                )
            }
        ) {
            Text(
                text = "Click To Authenticate !"
            )
        }
        promptResult?.let { result ->
            when(result) {
                is BiometricResult.AuthenticationError -> {
                    Toast.makeText(
                        context,
                        result.errorString,
                        Toast.LENGTH_LONG
                        ).show()
                }
                BiometricResult.AuthenticationFailed -> {
                    Toast.makeText(
                        context,
                        "Authentication Failed ",
                        Toast.LENGTH_LONG
                    ).show()
                }
                BiometricResult.AuthenticationNotSet -> {
                    Toast.makeText(
                        context,
                        "Authentication Not set",
                        Toast.LENGTH_LONG
                    ).show()
                }
                BiometricResult.AuthenticationSuccess -> {
                    Toast.makeText(
                        context,
                        "Authentication Success",
                        Toast.LENGTH_LONG
                    ).show()
                }
                BiometricResult.FeatureUnavailable -> {
                    Toast.makeText(
                        context,
                        "Feature Unavailable",
                        Toast.LENGTH_LONG
                    ).show()
                }
                BiometricResult.HardwareUnavailable -> {
                    Toast.makeText(
                        context,
                        "Hardware Unavailable",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}