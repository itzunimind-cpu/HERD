package com.motisoft.herd.ui.screens.cowdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.motisoft.herd.ui.components.HerdTopBar
import com.motisoft.herd.ui.components.SectionMenuRow
import com.motisoft.herd.ui.theme.Cream

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
    Scaffold(
        containerColor = Cream,
        topBar = { HerdTopBar(title = "टॅग क्रमांक $tag", onBack = onBack, dark = true) },
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SectionMenuRow(Icons.Default.Info, "गाय माहिती", onOpenCowInformation)
            SectionMenuRow(Icons.Default.Favorite, "प्रजनन माहिती", onOpenBreeding)
            SectionMenuRow(Icons.Default.WaterDrop, "दूध नोंद", onOpenMilk)
            SectionMenuRow(Icons.Default.LocalHospital, "आरोग्य अद्यतन", onOpenHealth)
            SectionMenuRow(Icons.Default.CalendarMonth, "दैनंदिन माहिती", onOpenDailyLog)
        }
    }
}
