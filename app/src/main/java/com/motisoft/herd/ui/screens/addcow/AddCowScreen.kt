package com.motisoft.herd.ui.screens.addcow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.motisoft.herd.ui.components.HerdTextField
import com.motisoft.herd.ui.components.HerdTopBar
import com.motisoft.herd.ui.components.PrimaryButton
import com.motisoft.herd.ui.theme.Cream
import com.motisoft.herd.ui.theme.DarkBrown
import com.motisoft.herd.ui.theme.Terracotta

@Composable
fun AddCowScreen(
    onDone: () -> Unit,
    onBack: () -> Unit,
    viewModel: AddCowViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = Cream,
        topBar = { HerdTopBar(title = "नवीन गाय जोडा", onBack = onBack) },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            HerdTextField(
                value = uiState.tag,
                onValueChange = viewModel::onTagChange,
                label = "टॅग क्रमांक",
                modifier = Modifier.padding(bottom = 12.dp),
            )
            HerdTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                label = "नाव",
                modifier = Modifier.padding(bottom = 12.dp),
            )
            HerdTextField(
                value = uiState.breed,
                onValueChange = viewModel::onBreedChange,
                label = "जात",
                modifier = Modifier.padding(bottom = 12.dp),
            )
            HerdTextField(
                value = uiState.birthDate,
                onValueChange = viewModel::onBirthDateChange,
                label = "जन्मतारीख",
                placeholder = "YYYY-MM-DD",
                modifier = Modifier.padding(bottom = 12.dp),
            )

            Text("लिंग", style = MaterialTheme.typography.labelMedium, color = DarkBrown)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 6.dp, bottom = 12.dp),
            ) {
                listOf("मादी", "नर").forEach { option ->
                    FilterChip(
                        selected = uiState.gender == option,
                        onClick = { viewModel.onGenderChange(option) },
                        label = { Text(option) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Terracotta),
                    )
                }
            }

            HerdTextField(
                value = uiState.weight,
                onValueChange = viewModel::onWeightChange,
                label = "वजन (किलो)",
                keyboardType = KeyboardType.Decimal,
                modifier = Modifier.padding(bottom = 12.dp),
            )
            HerdTextField(
                value = uiState.lineageTag,
                onValueChange = viewModel::onLineageTagChange,
                label = "आईचा/वडिलांचा टॅग (ऐच्छिक)",
                modifier = Modifier.padding(bottom = 12.dp),
            )

            uiState.errorMessage?.let { error ->
                Text(error, color = Terracotta, style = MaterialTheme.typography.labelMedium)
            }

            PrimaryButton(
                text = "जतन करा",
                onClick = { viewModel.save(onDone) },
                loading = uiState.isSaving,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            )
        }
    }
}
