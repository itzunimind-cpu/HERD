package com.motisoft.herd.ui.screens.milk

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.motisoft.herd.ui.components.StatCard
import com.motisoft.herd.ui.theme.Cream
import com.motisoft.herd.ui.theme.DarkBrown
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MilkRecordScreen(
    tag: String,
    onBack: () -> Unit,
    viewModel: MilkRecordViewModel = hiltViewModel(),
) {
    val records by viewModel.records.collectAsState()
    var showAddSheet by remember { mutableStateOf(false) }
    val latest = records.firstOrNull()

    Scaffold(
        containerColor = Cream,
        topBar = { HerdTopBar(title = "दूध नोंद", onBack = onBack, dark = true) },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                StatCard(
                    label = "सकाळचे दूध",
                    value = latest?.let { "${it.morningYield} लि" } ?: "-",
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    label = "संध्याकाळचे दूध",
                    value = latest?.let { "${it.eveningYield} लि" } ?: "-",
                    modifier = Modifier.weight(1f),
                )
            }

            Text(
                "मागील नोंदी",
                style = MaterialTheme.typography.titleMedium,
                color = DarkBrown,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
            )
            records.forEach { record ->
                HerdCard(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    LabelValueRow("तारीख", record.date.toString())
                    LabelValueRow("सकाळ / संध्याकाळ", "${record.morningYield} / ${record.eveningYield} लि")
                    LabelValueRow("फॅट%", record.fatPct?.toString() ?: "-")
                    LabelValueRow("SNF%", record.snfPct?.toString() ?: "-")
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
        AddMilkRecordSheet(
            onDismiss = { showAddSheet = false },
            onSave = { date, morning, evening, fat, snf ->
                viewModel.addRecord(date, morning, evening, fat, snf)
                showAddSheet = false
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddMilkRecordSheet(
    onDismiss: () -> Unit,
    onSave: (LocalDate, Double, Double, Double?, Double?) -> Unit,
) {
    var dateText by remember { mutableStateOf("") }
    var morning by remember { mutableStateOf("") }
    var evening by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var snf by remember { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("आजची दूध नोंद", style = MaterialTheme.typography.titleMedium, color = DarkBrown)
            HerdTextField(dateText, { dateText = it }, "तारीख", placeholder = "YYYY-MM-DD", modifier = Modifier.padding(top = 12.dp, bottom = 12.dp))
            HerdTextField(morning, { morning = it }, "सकाळचे दूध (लि)", keyboardType = KeyboardType.Decimal, modifier = Modifier.padding(bottom = 12.dp))
            HerdTextField(evening, { evening = it }, "संध्याकाळचे दूध (लि)", keyboardType = KeyboardType.Decimal, modifier = Modifier.padding(bottom = 12.dp))
            HerdTextField(fat, { fat = it }, "फॅट% (ऐच्छिक)", keyboardType = KeyboardType.Decimal, modifier = Modifier.padding(bottom = 12.dp))
            HerdTextField(snf, { snf = it }, "SNF% (ऐच्छिक)", keyboardType = KeyboardType.Decimal, modifier = Modifier.padding(bottom = 16.dp))
            PrimaryButton(
                text = "जतन करा",
                onClick = {
                    val date = runCatching { LocalDate.parse(dateText.trim()) }.getOrNull()
                    val m = morning.trim().toDoubleOrNull()
                    val e = evening.trim().toDoubleOrNull()
                    if (date != null && m != null && e != null) {
                        onSave(date, m, e, fat.trim().toDoubleOrNull(), snf.trim().toDoubleOrNull())
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            )
        }
    }
}
