package com.motisoft.herd.ui.screens.health

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthUpdateScreen(
    tag: String,
    onBack: () -> Unit,
    viewModel: HealthUpdateViewModel = hiltViewModel(),
) {
    val status by viewModel.status.collectAsState()
    val vaccinations by viewModel.vaccinations.collectAsState()
    val illnessLog by viewModel.illnessLog.collectAsState()
    var addSheetMode by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = Cream,
        topBar = { HerdTopBar(title = "आरोग्य अद्यतन", onBack = onBack) },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            Text("सद्य स्थिती", style = MaterialTheme.typography.labelMedium, color = DarkBrown)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 6.dp, bottom = 16.dp)) {
                listOf("निरोगी", "उपचार सुरू", "आजारी").forEach { option ->
                    FilterChip(
                        selected = status?.currentStatus == option,
                        onClick = { viewModel.setStatus(option) },
                        label = { Text(option) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Terracotta),
                    )
                }
            }

            Text("लसीकरण वेळापत्रक", style = MaterialTheme.typography.titleMedium, color = DarkBrown, modifier = Modifier.padding(bottom = 8.dp))
            vaccinations.forEach { v ->
                HerdCard(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    LabelValueRow("लस", v.vaccineName)
                    LabelValueRow("तारीख", v.date.toString())
                }
            }
            PrimaryButton(text = "लस जोडा", onClick = { addSheetMode = "vaccination" }, modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp))

            Text("आजार नोंद", style = MaterialTheme.typography.titleMedium, color = DarkBrown, modifier = Modifier.padding(bottom = 8.dp))
            illnessLog.forEach { entry ->
                HerdCard(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    LabelValueRow("वर्णन", entry.description)
                    LabelValueRow("तारीख", entry.date.toString())
                    LabelValueRow("उपचार", entry.treatment)
                }
            }
            PrimaryButton(text = "आजार नोंद जोडा", onClick = { addSheetMode = "illness" }, modifier = Modifier.fillMaxWidth())
        }
    }

    when (addSheetMode) {
        "vaccination" -> AddVaccinationSheet(
            onDismiss = { addSheetMode = null },
            onSave = { name, date ->
                viewModel.addVaccination(name, date)
                addSheetMode = null
            },
        )
        "illness" -> AddIllnessSheet(
            onDismiss = { addSheetMode = null },
            onSave = { description, date, treatment ->
                viewModel.addIllnessEntry(description, date, treatment)
                addSheetMode = null
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddVaccinationSheet(onDismiss: () -> Unit, onSave: (String, LocalDate) -> Unit) {
    var name by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf("") }
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("नवीन लस नोंद", style = MaterialTheme.typography.titleMedium, color = DarkBrown)
            HerdTextField(name, { name = it }, "लसीचे नाव", modifier = Modifier.padding(top = 12.dp, bottom = 12.dp))
            HerdTextField(dateText, { dateText = it }, "तारीख", placeholder = "YYYY-MM-DD", modifier = Modifier.padding(bottom = 16.dp))
            PrimaryButton(
                text = "जतन करा",
                onClick = {
                    val date = runCatching { LocalDate.parse(dateText.trim()) }.getOrNull()
                    if (name.isNotBlank() && date != null) onSave(name.trim(), date)
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddIllnessSheet(onDismiss: () -> Unit, onSave: (String, LocalDate, String) -> Unit) {
    var description by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf("") }
    var treatment by remember { mutableStateOf("") }
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("नवीन आजार नोंद", style = MaterialTheme.typography.titleMedium, color = DarkBrown)
            HerdTextField(description, { description = it }, "वर्णन", modifier = Modifier.padding(top = 12.dp, bottom = 12.dp))
            HerdTextField(dateText, { dateText = it }, "तारीख", placeholder = "YYYY-MM-DD", modifier = Modifier.padding(bottom = 12.dp))
            HerdTextField(treatment, { treatment = it }, "उपचार", modifier = Modifier.padding(bottom = 16.dp))
            PrimaryButton(
                text = "जतन करा",
                onClick = {
                    val date = runCatching { LocalDate.parse(dateText.trim()) }.getOrNull()
                    if (description.isNotBlank() && date != null) onSave(description.trim(), date, treatment.trim())
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            )
        }
    }
}
