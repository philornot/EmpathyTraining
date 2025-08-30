package com.empathytraining.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = EmpathyPrimary,
    onPrimary = EmpathyOnPrimary,
    primaryContainer = EmpathyPrimaryLight,
    onPrimaryContainer = EmpathyPrimaryDark,
    secondary = EmpathySecondary,
    onSecondary = EmpathyOnSecondary,
    secondaryContainer = EmpathySecondaryLight,
    onSecondaryContainer = EmpathySecondaryDark,
    tertiary = EmpathyTertiary,
    onTertiary = EmpathyOnTertiary,
    tertiaryContainer = EmpathyTertiaryLight,
    onTertiaryContainer = EmpathyTertiaryDark,
    error = EmpathyError,
    onError = EmpathyOnError,
    errorContainer = EmpathyErrorLight,
    onErrorContainer = EmpathyErrorDark,
    background = EmpathyBackground,
    onBackground = EmpathyOnBackground,
    surface = EmpathySurface,
    onSurface = EmpathyOnSurface,
    surfaceVariant = EmpathySurfaceVariant,
    onSurfaceVariant = EmpathyOnSurface,
    outline = EmpathyOutline,
    outlineVariant = EmpathyOutlineVariant
)

private val DarkColorScheme = darkColorScheme(
    primary = EmpathyPrimaryLight,
    onPrimary = EmpathyPrimaryDark,
    primaryContainer = EmpathyPrimaryDark,
    onPrimaryContainer = EmpathyPrimaryLight,
    secondary = EmpathySecondaryLight,
    onSecondary = EmpathySecondaryDark,
    secondaryContainer = EmpathySecondaryDark,
    onSecondaryContainer = EmpathySecondaryLight,
    tertiary = EmpathyTertiaryLight,
    onTertiary = EmpathyTertiaryDark,
    tertiaryContainer = EmpathyTertiaryDark,
    onTertiaryContainer = EmpathyTertiaryLight,
    error = EmpathyError,
    onError = EmpathyOnError,
    errorContainer = EmpathyErrorDark,
    onErrorContainer = EmpathyErrorLight,
    background = EmpathyDarkBackground,
    onBackground = EmpathyDarkOnBackground,
    surface = EmpathyDarkSurface,
    onSurface = EmpathyDarkOnSurface,
    surfaceVariant = EmpathyDarkSurface,
    onSurfaceVariant = EmpathyDarkOnSurface,
    outline = EmpathyOutline,
    outlineVariant = EmpathyOutlineVariant
)

private val EmpathyTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ), headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ), headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 26.sp
    ), titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ), titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ), bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ), bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ), bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ), labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
)

@Composable
fun EmpathyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // Enable edge-to-edge display for all API levels
            WindowCompat.setDecorFitsSystemWindows(window, false)

            // Get the WindowInsetsController
            val insetsController = WindowCompat.getInsetsController(window, view)

            // Set the appearance of system bars based on theme
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme

            // Set transparent system bars for edge-to-edge experience
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.setDecorFitsSystemWindows(false)
            }

            // Make system bars transparent
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            window.navigationBarColor = android.graphics.Color.TRANSPARENT
        }
    }

    MaterialTheme(
        colorScheme = colorScheme, typography = EmpathyTypography, content = content
    )
}