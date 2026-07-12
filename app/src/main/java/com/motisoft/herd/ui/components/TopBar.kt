package com.motisoft.herd.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.motisoft.herd.ui.theme.Cream
import com.motisoft.herd.ui.theme.DarkBrown

// Home/Sign In/Add Cow use the cream header; Cow Detail and its 5 section
// screens use the inverted dark-brown header per the design spec ("Header
// as above" on each section screen refers back to Cow Detail's dark header).
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HerdTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    dark: Boolean = false,
) {
    val containerColor = if (dark) DarkBrown else Cream
    val contentColor = if (dark) Cream else DarkBrown
    CenterAlignedTopAppBar(
        title = { Text(title, style = MaterialTheme.typography.titleMedium) },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack, modifier = Modifier.padding(4.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = contentColor)
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = containerColor,
            titleContentColor = contentColor,
        ),
    )
}
