package com.example.cinescope

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cinescope.app.CineScopeApp
import com.example.cinescope.ui.theme.CineScopeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CineScopeTheme {
                CineScopeApp()
            }
        }
    }
}
