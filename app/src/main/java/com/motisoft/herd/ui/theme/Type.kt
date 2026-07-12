package com.motisoft.herd.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Uses the platform sans-serif family rather than a downloaded Google Font: Android's
// system text renderer automatically substitutes its bundled Noto Sans Devanagari for
// Devanagari codepoints regardless of the requested typeface, so Marathi still renders
// correctly with zero network dependency - important for an offline-first field app.
val HerdFontFamily = FontFamily.SansSerif

val HerdTypography = Typography(
    headlineMedium = TextStyle(fontFamily = HerdFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 26.sp),
    headlineSmall = TextStyle(fontFamily = HerdFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp),
    titleMedium = TextStyle(fontFamily = HerdFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 19.sp),
    titleSmall = TextStyle(fontFamily = HerdFontFamily, fontWeight = FontWeight.Bold, fontSize = 17.sp),
    bodyLarge = TextStyle(fontFamily = HerdFontFamily, fontWeight = FontWeight.Bold, fontSize = 15.sp),
    bodyMedium = TextStyle(fontFamily = HerdFontFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp),
    labelLarge = TextStyle(fontFamily = HerdFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp),
    labelMedium = TextStyle(fontFamily = HerdFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
    labelSmall = TextStyle(fontFamily = HerdFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
)
