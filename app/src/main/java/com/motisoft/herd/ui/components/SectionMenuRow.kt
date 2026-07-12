package com.motisoft.herd.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.motisoft.herd.ui.theme.Cream
import com.motisoft.herd.ui.theme.DarkBrown
import com.motisoft.herd.ui.theme.HerdDimens
import com.motisoft.herd.ui.theme.Terracotta

// Each of the 5 section rows is its own tappable "large card" per the design
// spec (18dp radius, 2.5dp dark-brown border, shadow) - not a flat list row.
@Composable
fun SectionMenuRow(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HerdCard(modifier = modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(HerdDimens.IconBadgeSize)
                    .background(Terracotta, RoundedCornerShape(HerdDimens.IconBadgeRadius)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = Cream)
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = DarkBrown,
                modifier = Modifier.weight(1f),
            )
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = DarkBrown)
        }
    }
}
