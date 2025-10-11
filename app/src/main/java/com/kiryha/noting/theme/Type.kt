package com.kiryha.noting.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.kiryha.noting.R

// Set of Material typography styles to start with
fun getTypography(): Typography{
    return Typography(
        bodyLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.5.sp
        ),
        bodySmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            lineHeight = 15.sp,
            letterSpacing = 0.5.sp
        ),
        titleLarge = TextStyle(
            fontFamily = NType87,
            fontWeight = FontWeight.Normal,
            fontSize = 36.sp,
            lineHeight = 42.sp,
            letterSpacing = 0.sp
        ),
        labelSmall = TextStyle(
            fontFamily = NType87,
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
            lineHeight = 25.sp,
            letterSpacing = 0.sp
        ),
        labelLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
            lineHeight = 26.sp,
            letterSpacing = 0.5.sp,
        ),
        labelMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 17.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.5.sp
        ),
        titleMedium = TextStyle(
            fontFamily = NType87,
            fontWeight = FontWeight.Normal,
            fontSize = 32.sp,
            lineHeight = 38.sp,
            letterSpacing = 0.sp
        ),
    )
}

val NType87 = FontFamily(
    Font(R.font.ntype87)
)