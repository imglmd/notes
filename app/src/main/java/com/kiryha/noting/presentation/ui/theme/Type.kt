package com.kiryha.noting.presentation.ui.theme

import androidx.compose.material3.Typography
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
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        ),
        titleLarge = TextStyle(
            fontFamily = NType87,
            fontWeight = FontWeight.Normal,
            fontSize = 30.sp,
            lineHeight = 40.sp,
            letterSpacing = 0.sp
        ),
    )
}

val NType87 = FontFamily(
    Font(R.font.ntype87)
)