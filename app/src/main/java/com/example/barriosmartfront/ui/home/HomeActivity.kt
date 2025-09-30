package com.example.barriosmartfront.ui.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text

import com.example.barriosmartfront.ui.theme.SeguridadTheme

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SeguridadTheme {
                Text("Bienvenido a BarrioSmart")
            }
        }
    }
}

