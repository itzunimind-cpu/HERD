package com.motisoft.herd.ui.screens.cowdetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.motisoft.herd.ui.components.HerdTopBar
import com.motisoft.herd.ui.components.SectionMenuRow
import com.motisoft.herd.ui.theme.Cream
import com.motisoft.herd.ui.theme.DarkBrown
import com.motisoft.herd.ui.theme.SupportingText

@Composable
fun CowDetailScreen(
    tag: String,
    onBack: () -> Unit,
    onOpenCowInformation: () -> Unit,
    onOpenBreeding: () -> Unit,
    onOpenMilk: () -> Unit,
    onOpenHealth: () -> Unit,
    onOpenDailyLog: () -> Unit,
    viewModel: CowDetailViewModel = hiltViewModel(),
) {
    val cow by viewModel.cow.collectAsState()

    Scaffold(
        containerColor = Cream,
        topBar = { HerdTopBar(title = cow?.name ?: tag, onBack = onBack) },
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(20.dp)) {
            Text(
                "टॅग: $tag",
                style = MaterialTheme.typography.labelMedium,
                color = SupportingText,
                modifier = Modifier.padding(bottom = 16.dp),
            )

            SectionMenuRow(Icons.Default.Info, "गाय माहिती", onOpenCowInformation)
            SectionMenuRow(Icons.Default.Favorite, "प्रजनन माहिती", onOpenBreeding)
            SectionMenuRow(Icons.Default.WaterDrop, "दूध नोंद", onOpenMilk)
            SectionMenuRow(Icons.Default.LocalHospital, "आरोग्य अद्यतन", onOpenHealth)
            SectionMenuRow(Icons.Default.CalendarMonth, "दैनंदिन माहिती", onOpenDailyLog)
        }
    }
}
