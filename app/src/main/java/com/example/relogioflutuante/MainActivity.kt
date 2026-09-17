package com.example.relogioflutuante

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.relogioflutuante.ui.FloatingClockApp
import com.example.relogioflutuante.ui.theme.FloatingClockTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FloatingClockTheme {
                FloatingClockApp()
            }
        }
    }
}
