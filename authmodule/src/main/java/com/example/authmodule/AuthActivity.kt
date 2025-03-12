package com.example.authmodule

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.xcamera.authentication.AuthenticationManager
import com.example.xcamera.authentication.AuthenticationScreen
import com.example.authmodule.ui.theme.XCameraTheme

class AuthActivity : AppCompatActivity() {
    private val manager by lazy {
        com.example.xcamera.authentication.AuthenticationManager(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            XCameraTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    com.example.xcamera.authentication.AuthenticationScreen(
                        manager,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

