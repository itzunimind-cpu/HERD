package com.motisoft.herd.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.motisoft.herd.ui.components.HerdCard
import com.motisoft.herd.ui.components.HerdTextField
import com.motisoft.herd.ui.components.HerdTimePickerDialog
import com.motisoft.herd.ui.components.HerdTopBar
import com.motisoft.herd.ui.components.PrimaryButton
import com.motisoft.herd.ui.components.StatCard
import com.motisoft.herd.ui.theme.Cream
import com.motisoft.herd.ui.theme.DarkBrown
import com.motisoft.herd.ui.theme.HerdDimens
import com.motisoft.herd.ui.theme.SupportingText
import com.motisoft.herd.ui.theme.Terracotta

@Composable
fun DashboardScreen(
    onBack: () -> Unit,
    onSignedOut: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var editingSession by remember { mutableStateOf<ReminderTarget?>(null) }

    Scaffold(
        containerColor = Cream,
        topBar = { HerdTopBar(title = "डॅशबोर्ड", onBack = onBack) },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(HerdDimens.ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            StatCard(label = "एकूण गायी", value = uiState.cowCount.toString())

            HerdCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("वैयक्तिक माहिती", style = MaterialTheme.typography.titleSmall, color = DarkBrown)
                    Text(uiState.email, style = MaterialTheme.typography.bodyMedium, color = SupportingText)

                    HerdTextField(value = uiState.name, onValueChange = viewModel::onNameChange, label = "नाव")
                    HerdTextField(
                        value = uiState.phone,
                        onValueChange = viewModel::onPhoneChange,
                        label = "फोन नंबर",
                        keyboardType = KeyboardType.Phone,
                    )
                    HerdTextField(value = uiState.farmName, onValueChange = viewModel::onFarmNameChange, label = "गोठ्याचे नाव")

                    uiState.profileSavedMessage?.let {
                        Text(it, style = MaterialTheme.typography.labelMedium, color = Terracotta)
                    }
                    PrimaryButton(text = "जतन करा", onClick = viewModel::saveProfile, loading = uiState.isSavingProfile)
                }
            }

            HerdCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("पासवर्ड बदला", style = MaterialTheme.typography.titleSmall, color = DarkBrown)
                    HerdTextField(
                        value = uiState.newPassword,
                        onValueChange = viewModel::onNewPasswordChange,
                        label = "नवीन पासवर्ड",
                        keyboardType = KeyboardType.Password,
                        visualTransformation = PasswordVisualTransformation(),
                    )
                    HerdTextField(
                        value = uiState.confirmPassword,
                        onValueChange = viewModel::onConfirmPasswordChange,
                        label = "पासवर्डची पुष्टी करा",
                        keyboardType = KeyboardType.Password,
                        visualTransformation = PasswordVisualTransformation(),
                    )
                    uiState.passwordError?.let {
                        Text(it, style = MaterialTheme.typography.labelMedium, color = Terracotta)
                    }
                    uiState.passwordChangedMessage?.let {
                        Text(it, style = MaterialTheme.typography.labelMedium, color = Terracotta)
                    }
                    PrimaryButton(text = "पासवर्ड बदला", onClick = viewModel::changePassword, loading = uiState.isChangingPassword)
                }
            }

            HerdCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("सूचना", style = MaterialTheme.typography.titleSmall, color = DarkBrown)
                    ReminderTimeRow(
                        label = "सकाळची आठवण",
                        hour = uiState.morningReminderHour,
                        minute = uiState.morningReminderMinute,
                        onClick = { editingSession = ReminderTarget.MORNING },
                    )
                    ReminderTimeRow(
                        label = "संध्याकाळची आठवण",
                        hour = uiState.eveningReminderHour,
                        minute = uiState.eveningReminderMinute,
                        onClick = { editingSession = ReminderTarget.EVENING },
                    )
                }
            }

            PrimaryButton(text = "साइन आउट करा", onClick = { viewModel.signOut(onSignedOut) })
        }
    }

    editingSession?.let { target ->
        val (hour, minute) = when (target) {
            ReminderTarget.MORNING -> uiState.morningReminderHour to uiState.morningReminderMinute
            ReminderTarget.EVENING -> uiState.eveningReminderHour to uiState.eveningReminderMinute
        }
        HerdTimePickerDialog(
            initialHour = hour,
            initialMinute = minute,
            onDismiss = { editingSession = null },
            onConfirm = { newHour, newMinute ->
                when (target) {
                    ReminderTarget.MORNING -> viewModel.setMorningReminderTime(newHour, newMinute)
                    ReminderTarget.EVENING -> viewModel.setEveningReminderTime(newHour, newMinute)
                }
                editingSession = null
            },
        )
    }
}

private enum class ReminderTarget { MORNING, EVENING }

@Composable
private fun ReminderTimeRow(label: String, hour: Int, minute: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = DarkBrown)
        TextButton(onClick = onClick) {
            Text("%02d:%02d".format(hour, minute), color = Terracotta)
        }
    }
}
