package com.motisoft.herd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.motisoft.herd.ui.navigation.HerdNavGraph
import com.motisoft.herd.ui.theme.HerdTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HerdApp()
        }
    }
}

@Composable
private fun HerdApp() {
    HerdTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            HerdNavGraph()
        }
    }
}
