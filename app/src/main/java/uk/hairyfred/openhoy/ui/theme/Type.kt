package uk.hairyfred.openhoy.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val OpenHoyTypography = Typography(
    displayLarge = TextStyle(fontWeight = FontWeight.Black, fontSize = 96.sp),
    displayMedium = TextStyle(fontWeight = FontWeight.Black, fontSize = 64.sp),
    displaySmall = TextStyle(fontWeight = FontWeight.Bold, fontSize = 48.sp),
    headlineLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 40.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp),
    bodyLarge = TextStyle(fontSize = 18.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp),
)
