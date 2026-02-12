package com.example.myapplication.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.Purple


object AppColors {
    val Purple = Color(0xFF6A1B9A)
    val DarkPurple = Color(0xFF1A0033)
    val LightPurple = Color(0xFF9575CD)
    val VeryLightPurple = Color(0xFFF3E5F5)
    val White = Color.White
    val Gray = Color.Gray
    val Red = Color(0xFFD32F2F)
    val DisabledGray = Color(0xFFBDBDBD)

    val BackGround1 = Color(0xFF512DA8)
    val BackGround2 = Color(0xFF9575CD)

    val backgroundGradient = listOf(BackGround1, BackGround2)
}


val AppTypography = Typography(
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        color = AppColors.Purple
    ),

    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),


    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),

    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    )
)

private val DarkColorScheme = darkColorScheme(
    primary = Purple,
    secondary = LightPurple,
    tertiary = BackGround2,
    background = BackGround1,
    surface = White,
    onPrimary = White,
    onSecondary = White,
    onTertiary = White,
    onBackground = DarkPurple,
    onSurface = DarkPurple,
    error = Red,
    onError = White
)

private val LightColorScheme = lightColorScheme(
    primary = Purple,
    secondary = LightPurple,
    tertiary = BackGround2,
    background = BackGround1,
    surface = White,
    onPrimary = White,
    onSecondary = White,
    onTertiary = White,
    onBackground = DarkPurple,
    onSurface = DarkPurple,
    error = Red,
    onError = White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
   // dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}



