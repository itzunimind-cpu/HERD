package com.motisoft.herd.ui.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.motisoft.herd.ui.theme.Cream
import com.motisoft.herd.ui.theme.DarkBrown
import com.motisoft.herd.ui.theme.Terracotta

@Composable
fun SplashScreen(
    onSignedIn: () -> Unit,
    onSignedOut: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val isSignedIn by viewModel.isSignedIn.collectAsState()

    LaunchedEffect(isSignedIn) {
        when (isSignedIn) {
            true -> onSignedIn()
            false -> onSignedOut()
            null -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "Herd",
            style = MaterialTheme.typography.headlineMedium,
            color = DarkBrown,
        )
        Text(
            "गुरांचे व्यवस्थापन",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkBrown,
            modifier = Modifier.padding(bottom = 32.dp),
        )
        CircularProgressIndicator(color = Terracotta)
    }
}
