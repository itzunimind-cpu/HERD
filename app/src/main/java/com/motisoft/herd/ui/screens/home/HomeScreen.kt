package com.motisoft.herd.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.motisoft.herd.data.local.entity.CowEntity
import com.motisoft.herd.ui.components.HerdCard
import com.motisoft.herd.ui.theme.Cream
import com.motisoft.herd.ui.theme.DarkBrown
import com.motisoft.herd.ui.theme.HerdDimens
import com.motisoft.herd.ui.theme.Sage
import com.motisoft.herd.ui.theme.SupportingText
import com.motisoft.herd.ui.theme.Terracotta

@Composable
fun HomeScreen(
    onAddCow: () -> Unit,
    onScan: () -> Unit,
    onCowClick: (String) -> Unit,
    onOpenDashboard: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val cows by viewModel.cows.collectAsState()
    var showQuickMilkSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Cream,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onScan,
                shape = CircleShape,
                containerColor = DarkBrown,
                contentColor = Cream,
                modifier = Modifier
                    .size(HerdDimens.FabSize)
                    .border(HerdDimens.FabBorderWidth, Cream, CircleShape),
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = "गाय स्कॅन करा")
            }
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(HerdDimens.ScreenPadding)) {
            HomeHeader(
                onAddCow = onAddCow,
                onOpenDashboard = onOpenDashboard,
                onOpenQuickMilk = { showQuickMilkSheet = true },
            )

            if (cows.isEmpty()) {
                EmptyState(onAddCow = onAddCow)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 96.dp),
                ) {
                    items(cows, key = { it.tag }) { cow ->
                        CowCard(cow = cow, onClick = { onCowClick(cow.tag) })
                    }
                }
            }
        }
    }

    if (showQuickMilkSheet) {
        QuickMilkEntrySheet(
            cows = cows,
            onDismiss = { showQuickMilkSheet = false },
            onCowSelected = { tag ->
                showQuickMilkSheet = false
                onCowClick(tag)
            },
        )
    }
}

@Composable
private fun HomeHeader(onAddCow: () -> Unit, onOpenDashboard: () -> Unit, onOpenQuickMilk: () -> Unit) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("आमचा गोठा", style = MaterialTheme.typography.headlineSmall, color = DarkBrown)
        androidx.compose.foundation.layout.Row {
            IconButton(onClick = onOpenDashboard) {
                Icon(Icons.Default.AccountCircle, contentDescription = "डॅशबोर्ड", tint = Terracotta)
            }
            IconButton(onClick = onOpenQuickMilk) {
                Icon(Icons.Default.WaterDrop, contentDescription = "आजचे दूध नोंदवा", tint = Terracotta)
            }
            IconButton(onClick = onAddCow) {
                Icon(Icons.Default.Add, contentDescription = "नवीन गाय जोडा", tint = Terracotta)
            }
        }
    }
}

@Composable
private fun EmptyState(onAddCow: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp)
            .border(HerdDimens.CardBorderWidth, Terracotta, RoundedCornerShape(HerdDimens.CardRadius))
            .clickable(onClick = onAddCow)
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Terracotta, modifier = Modifier.size(40.dp))
            Text(
                "अजून कोणतीही गाय जोडलेली नाही",
                style = MaterialTheme.typography.bodyLarge,
                color = DarkBrown,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 12.dp),
            )
            Text(
                "नवीन गाय जोडा",
                style = MaterialTheme.typography.labelMedium,
                color = Terracotta,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun CowCard(cow: CowEntity, onClick: () -> Unit) {
    HerdCard(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Sage, CircleShape)
                    .border(BorderStroke(2.dp, DarkBrown), CircleShape),
            )
            Column(modifier = Modifier.padding(start = 14.dp)) {
                Text(cow.name, style = MaterialTheme.typography.titleSmall, color = DarkBrown)
                Text("टॅग: ${cow.tag}", style = MaterialTheme.typography.labelSmall, color = SupportingText)
                Text(cow.breed, style = MaterialTheme.typography.labelSmall, color = SupportingText)
            }
        }
    }
}
