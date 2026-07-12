package com.motisoft.herd.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Material3 Shapes only has small/medium/large/extraLarge slots.
// small = inputs/badges (14-16dp), medium = cards (18dp) per the design spec.
val HerdShapes = Shapes(
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(18.dp),
    extraLarge = RoundedCornerShape(24.dp),
)
