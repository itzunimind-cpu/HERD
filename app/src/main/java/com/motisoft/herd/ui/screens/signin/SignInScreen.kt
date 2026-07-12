package com.motisoft.herd.ui.screens.signin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.motisoft.herd.ui.components.HerdTextField
import com.motisoft.herd.ui.components.PrimaryButton
import com.motisoft.herd.ui.theme.Cream
import com.motisoft.herd.ui.theme.DarkBrown
import com.motisoft.herd.ui.theme.Terracotta

@Composable
fun SignInScreen(
    onSignInSuccess: () -> Unit,
    viewModel: SignInViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
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

        HerdTextField(
            value = uiState.email,
            onValueChange = viewModel::onEmailChange,
            label = "वापरकर्तानाव",
            keyboardType = KeyboardType.Email,
            modifier = Modifier.padding(bottom = 16.dp),
        )
        HerdTextField(
            value = uiState.password,
            onValueChange = viewModel::onPasswordChange,
            label = "पासवर्ड",
            keyboardType = KeyboardType.Password,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.padding(bottom = 8.dp),
        )

        uiState.errorMessage?.let { error ->
            Text(
                error,
                color = Terracotta,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 16.dp),
            )
        }

        PrimaryButton(
            text = "साइन इन करा",
            onClick = { viewModel.signIn(onSignInSuccess) },
            loading = uiState.isLoading,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}
