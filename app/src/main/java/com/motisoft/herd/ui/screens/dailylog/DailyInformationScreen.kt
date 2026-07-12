package com.motisoft.herd.ui.screens.dailylog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyInformationScreen(
    tag: String,
    onBack: () -> Unit,
    viewModel: DailyInformationViewModel = hiltViewModel(),
) {
    val logs by viewModel.logs.collectAsState()
    var showAddSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Cream,
        topBar = { HerdTopBar(title = "दैनंदिन माहिती", onBack = onBack) },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            Text(
                "मागील नोंदी",
                style = MaterialTheme.typography.titleMedium,
                color = DarkBrown,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            logs.forEach { log ->
                HerdCard(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    LabelValueRow("तारीख", log.date.toString())
                    LabelValueRow("खाद्य", log.feed)
                    LabelValueRow("पाणी", log.water)
                    LabelValueRow("तापमान", log.temperature?.toString() ?: "-")
                    LabelValueRow("टीप", log.activityNotes)
                }
            }

            PrimaryButton(
                text = "आजची नोंद जोडा",
                onClick = { showAddSheet = true },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            )
        }
    }

    if (showAddSheet) {
        AddDailyLogSheet(
            onDismiss = { showAddSheet = false },
            onSave = { date, feed, water, temp, notes ->
                viewModel.addLog(date, feed, water, temp, notes)
                showAddSheet = false
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddDailyLogSheet(
    onDismiss: () -> Unit,
    onSave: (LocalDate, String, String, Double?, String) -> Unit,
) {
    var dateText by remember { mutableStateOf("") }
    var feed by remember { mutableStateOf("") }
    var water by remember { mutableStateOf("") }
    var temperature by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("आजची दैनंदिन नोंद", style = MaterialTheme.typography.titleMedium, color = DarkBrown)
            HerdTextField(dateText, { dateText = it }, "तारीख", placeholder = "YYYY-MM-DD", modifier = Modifier.padding(top = 12.dp, bottom = 12.dp))
            HerdTextField(feed, { feed = it }, "खाद्य", modifier = Modifier.padding(bottom = 12.dp))
            HerdTextField(water, { water = it }, "पाणी", modifier = Modifier.padding(bottom = 12.dp))
            HerdTextField(temperature, { temperature = it }, "तापमान (ऐच्छिक)", keyboardType = KeyboardType.Decimal, modifier = Modifier.padding(bottom = 12.dp))
            HerdTextField(notes, { notes = it }, "टीप", modifier = Modifier.padding(bottom = 16.dp))
            PrimaryButton(
                text = "जतन करा",
                onClick = {
                    val date = runCatching { LocalDate.parse(dateText.trim()) }.getOrNull()
                    if (date != null && feed.isNotBlank() && water.isNotBlank()) {
                        onSave(date, feed.trim(), water.trim(), temperature.trim().toDoubleOrNull(), notes.trim())
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            )
        }
    }
}
