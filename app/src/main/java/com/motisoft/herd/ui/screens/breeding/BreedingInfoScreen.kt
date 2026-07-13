package com.motisoft.herd.ui.screens.breeding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.motisoft.herd.ui.components.HerdCard
import com.motisoft.herd.ui.components.HerdTextField
import com.motisoft.herd.ui.components.LabelValueRow
import com.motisoft.herd.ui.components.HerdTopBar
import com.motisoft.herd.ui.components.PrimaryButton
import com.motisoft.herd.ui.theme.Cream
import com.motisoft.herd.ui.theme.DarkBrown
import com.motisoft.herd.ui.theme.Terracotta
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedingInfoScreen(
    tag: String,
    onBack: () -> Unit,
    viewModel: BreedingInfoViewModel = hiltViewModel(),
) {
    val info by viewModel.info.collectAsState()
    val calvingHistory by viewModel.calvingHistory.collectAsState()
    val draft by viewModel.draft.collectAsState()
    var showAddSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Cream,
        topBar = { HerdTopBar(title = "प्रजनन माहिती", onBack = onBack, dark = true) },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            Text("गर्भधारणा स्थिती", style = MaterialTheme.typography.labelMedium, color = DarkBrown)
            androidx.compose.foundation.layout.Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 6.dp, bottom = 16.dp),
            ) {
                listOf("गाभण", "रिकामी", "अनिश्चित").forEach { option ->
                    FilterChip(
                        selected = info?.pregnancyStatus == option,
                        onClick = { viewModel.savePregnancyStatus(option) },
                        label = { Text(option) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Terracotta),
                    )
                }
            }

            HerdCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("प्रजनन तपशील", style = MaterialTheme.typography.titleSmall, color = DarkBrown)
                    HerdTextField(
                        value = draft.lastHeatDate,
                        onValueChange = viewModel::onLastHeatDateChange,
                        label = "शेवटची माजाची तारीख",
                        placeholder = "YYYY-MM-DD",
                    )
                    HerdTextField(
                        value = draft.inseminationDate,
                        onValueChange = viewModel::onInseminationDateChange,
                        label = "रेतन तारीख",
                        placeholder = "YYYY-MM-DD",
                    )
                    HerdTextField(
                        value = draft.semenBreed,
                        onValueChange = viewModel::onSemenBreedChange,
                        label = "वीर्य/जातीचा प्रकार",
                    )
                    HerdTextField(
                        value = draft.pregnancyTestDate,
                        onValueChange = viewModel::onPregnancyTestDateChange,
                        label = "गर्भ तपासणी तारीख",
                        placeholder = "YYYY-MM-DD",
                    )
                    HerdTextField(
                        value = draft.expectedCalvingDate,
                        onValueChange = viewModel::onExpectedCalvingDateChange,
                        label = "अपेक्षित वेतांची तारीख (अंदाजे)",
                        placeholder = "YYYY-MM-DD",
                    )
                    draft.savedMessage?.let {
                        Text(it, style = MaterialTheme.typography.labelMedium, color = Terracotta)
                    }
                    PrimaryButton(text = "जतन करा", onClick = viewModel::saveDetails, loading = draft.isSaving)
                }
            }

            Text(
                "वेतांचा इतिहास",
                style = MaterialTheme.typography.titleMedium,
                color = DarkBrown,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
            )
            calvingHistory.forEach { entry ->
                HerdCard(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    LabelValueRow("तारीख", entry.calvingDate.toString())
                    LabelValueRow("वासराचे लिंग", entry.calfSex)
                }
            }

            PrimaryButton(
                text = "नवीन नोंद जोडा",
                onClick = { showAddSheet = true },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            )
        }
    }

    if (showAddSheet) {
        AddCalvingEntrySheet(
            onDismiss = { showAddSheet = false },
            onSave = { calfSex, date ->
                viewModel.addCalvingEntry(calfSex, date)
                showAddSheet = false
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddCalvingEntrySheet(
    onDismiss: () -> Unit,
    onSave: (String, LocalDate) -> Unit,
) {
    var calfSex by remember { mutableStateOf("मादी") }
    var dateText by remember { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("नवीन वेतांची नोंद", style = MaterialTheme.typography.titleMedium, color = DarkBrown)
            androidx.compose.foundation.layout.Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 12.dp, bottom = 12.dp),
            ) {
                listOf("मादी", "नर").forEach { option ->
                    FilterChip(
                        selected = calfSex == option,
                        onClick = { calfSex = option },
                        label = { Text(option) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Terracotta),
                    )
                }
            }
            HerdTextField(
                value = dateText,
                onValueChange = { dateText = it },
                label = "तारीख",
                placeholder = "YYYY-MM-DD",
                modifier = Modifier.padding(bottom = 16.dp),
            )
            PrimaryButton(
                text = "जतन करा",
                onClick = {
                    val date = runCatching { LocalDate.parse(dateText.trim()) }.getOrNull()
                    if (date != null) onSave(calfSex, date)
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            )
        }
    }
}
