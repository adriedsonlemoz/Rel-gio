package com.example.relogioflutuante

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.relogioflutuante.ui.FloatingClockApp
import com.example.relogioflutuante.ui.applyImmersiveMode
import com.example.relogioflutuante.ui.theme.FloatingClockTheme

class MainActivity : ComponentActivity() {
    private var permissionRefresh by mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyImmersiveMode(window)
        setContent {
            FloatingClockTheme {
                FloatingClockApp(permissionRefresh)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        permissionRefresh++
        applyImmersiveMode(window)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) applyImmersiveMode(window)
    }
}
