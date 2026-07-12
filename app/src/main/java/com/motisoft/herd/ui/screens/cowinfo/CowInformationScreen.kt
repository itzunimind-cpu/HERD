package com.motisoft.herd.ui.screens.cowinfo

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
import com.motisoft.herd.ui.components.HerdCard
import com.motisoft.herd.ui.components.HerdTextField
import com.motisoft.herd.ui.components.HerdTopBar
import com.motisoft.herd.ui.components.LabelValueRow
import com.motisoft.herd.ui.components.PrimaryButton
import com.motisoft.herd.ui.theme.Cream
import com.motisoft.herd.ui.theme.DarkBrown
import com.motisoft.herd.ui.theme.Terracotta

@Composable
fun CowInformationScreen(
    tag: String,
    onBack: () -> Unit,
    viewModel: CowInformationViewModel = hiltViewModel(),
) {
    val cow by viewModel.cow.collectAsState()
    val isEditing by viewModel.isEditing.collectAsState()
    val editState by viewModel.editState.collectAsState()

    Scaffold(
        containerColor = Cream,
        topBar = { HerdTopBar(title = "गाय माहिती", onBack = onBack, dark = true) },
    ) { padding ->
        val currentCow = cow
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            if (currentCow == null) {
                Text("लोड होत आहे...", color = DarkBrown)
                return@Column
            }

            if (isEditing) {
                HerdTextField(editState.name, viewModel::onNameChange, "नाव", modifier = Modifier.padding(bottom = 12.dp))
                HerdTextField(editState.breed, viewModel::onBreedChange, "जात", modifier = Modifier.padding(bottom = 12.dp))
                HerdTextField(
                    editState.birthDate,
                    viewModel::onBirthDateChange,
                    "जन्मतारीख",
                    placeholder = "YYYY-MM-DD",
                    modifier = Modifier.padding(bottom = 12.dp),
                )
                Text("लिंग", style = MaterialTheme.typography.labelMedium, color = DarkBrown)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 6.dp, bottom = 12.dp)) {
                    listOf("मादी", "नर").forEach { option ->
                        FilterChip(
                            selected = editState.gender == option,
                            onClick = { viewModel.onGenderChange(option) },
                            label = { Text(option) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Terracotta),
                        )
                    }
                }
                HerdTextField(
                    editState.weight,
                    viewModel::onWeightChange,
                    "वजन (किलो)",
                    keyboardType = KeyboardType.Decimal,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
                HerdTextField(
                    editState.lineageTag,
                    viewModel::onLineageTagChange,
                    "आईचा/वडिलांचा टॅग",
                    modifier = Modifier.padding(bottom = 12.dp),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PrimaryButton(
                        text = "रद्द करा",
                        onClick = viewModel::cancelEditing,
                        modifier = Modifier.weight(1f),
                    )
                    PrimaryButton(
                        text = "जतन करा",
                        onClick = { viewModel.save(currentCow) },
                        modifier = Modifier.weight(1f),
                    )
                }
            } else {
                HerdCard(modifier = Modifier.fillMaxWidth()) {
                    LabelValueRow("नाव", currentCow.name)
                    LabelValueRow("टॅग", currentCow.tag)
                    LabelValueRow("जात", currentCow.breed)
                    LabelValueRow("जन्मतारीख", currentCow.birthDate?.toString() ?: "-")
                    LabelValueRow("लिंग", currentCow.gender)
                    LabelValueRow("वजन", currentCow.weight?.let { "$it किलो" } ?: "-")
                    LabelValueRow("आईचा/वडिलांचा टॅग", currentCow.lineageTag ?: "-")
                }
                PrimaryButton(
                    text = "माहिती संपादित करा",
                    onClick = { viewModel.startEditing(currentCow) },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                )
            }
        }
    }
}
