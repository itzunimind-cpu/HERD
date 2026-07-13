package com.motisoft.herd.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import com.motisoft.herd.data.local.entity.CowEntity
import com.motisoft.herd.ui.components.HerdTextField
import com.motisoft.herd.ui.components.PrimaryButton
import com.motisoft.herd.ui.theme.DarkBrown
import com.motisoft.herd.ui.theme.SupportingText
import com.motisoft.herd.ui.theme.Terracotta

private enum class QuickMilkMode { TOTAL, PER_COW }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickMilkEntrySheet(
    cows: List<CowEntity>,
    onDismiss: () -> Unit,
    onCowSelected: (String) -> Unit,
    viewModel: QuickMilkEntryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var mode by remember { mutableStateOf(QuickMilkMode.TOTAL) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("आजचे दूध नोंदवा", style = MaterialTheme.typography.titleMedium, color = DarkBrown)

            androidx.compose.foundation.layout.Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 12.dp, bottom = 16.dp),
            ) {
                FilterChip(
                    selected = mode == QuickMilkMode.TOTAL,
                    onClick = { mode = QuickMilkMode.TOTAL },
                    label = { Text("एकूण नोंदवा") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Terracotta),
                )
                FilterChip(
                    selected = mode == QuickMilkMode.PER_COW,
                    onClick = { mode = QuickMilkMode.PER_COW },
                    label = { Text("गाय निवडून नोंदवा") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Terracotta),
                )
            }

            when (mode) {
                QuickMilkMode.TOTAL -> {
                    HerdTextField(
                        value = uiState.morningTotal,
                        onValueChange = viewModel::onMorningTotalChange,
                        label = "सकाळचे एकूण दूध (लि)",
                        keyboardType = KeyboardType.Decimal,
                        modifier = Modifier.padding(bottom = 12.dp),
                    )
                    HerdTextField(
                        value = uiState.eveningTotal,
                        onValueChange = viewModel::onEveningTotalChange,
                        label = "संध्याकाळचे एकूण दूध (लि)",
                        keyboardType = KeyboardType.Decimal,
                        modifier = Modifier.padding(bottom = 16.dp),
                    )
                    uiState.savedMessage?.let {
                        Text(it, style = MaterialTheme.typography.labelMedium, color = Terracotta)
                    }
                    PrimaryButton(
                        text = "जतन करा",
                        onClick = { viewModel.saveTotal(onDismiss) },
                        loading = uiState.isSaving,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    )
                }
                QuickMilkMode.PER_COW -> {
                    LazyColumn(modifier = Modifier.heightIn(max = 320.dp)) {
                        items(cows, key = { it.tag }) { cow ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onCowSelected(cow.tag) }
                                    .padding(vertical = 12.dp),
                            ) {
                                Text(cow.name, style = MaterialTheme.typography.titleSmall, color = DarkBrown)
                                Text("टॅग: ${cow.tag}", style = MaterialTheme.typography.labelSmall, color = SupportingText)
                            }
                        }
                    }
                }
            }
        }
    }
}
