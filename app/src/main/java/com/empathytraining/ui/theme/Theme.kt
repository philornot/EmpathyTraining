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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

/**
 * Light color scheme for Empathy Training app Uses warm, supportive colors
 * that promote empathy and calmness
 */
private val LightColorScheme = lightColorScheme(
    // Primary colors - main brand identity
    primary = EmpathyPrimary,
    onPrimary = EmpathyOnPrimary,
    primaryContainer = EmpathyPrimaryLight,
    onPrimaryContainer = EmpathyPrimaryDark,

    // Secondary colors - supporting elements
    secondary = EmpathySecondary,
    onSecondary = EmpathyOnSecondary,
    secondaryContainer = EmpathySecondaryLight,
    onSecondaryContainer = EmpathySecondaryDark,

    // Tertiary colors - accent elements
    tertiary = EmpathyTertiary,
    onTertiary = EmpathyOnTertiary,
    tertiaryContainer = EmpathyTertiaryLight,
    onTertiaryContainer = EmpathyTertiaryDark,

    // Error colors - validation and warnings
    error = EmpathyError,
    onError = EmpathyOnError,
    errorContainer = EmpathyErrorLight,
    onErrorContainer = EmpathyErrorDark,

    // Background colors - app backgrounds
    background = EmpathyBackground,
    onBackground = EmpathyOnBackground,

    // Surface colors - card and component backgrounds
    surface = EmpathySurface,
    onSurface = EmpathyOnSurface,
    surfaceVariant = EmpathySurfaceVariant,
    onSurfaceVariant = EmpathyOnSurface,

    // Outline colors - borders and dividers
    outline = EmpathyOutline,
    outlineVariant = EmpathyOutlineVariant,

    // Container colors - special containers
    surfaceTint = EmpathyPrimary,
    inverseSurface = EmpathyOnBackground,
    inverseOnSurface = EmpathyBackground,
    inversePrimary = EmpathyPrimaryLight
)

/**
 * Dark color scheme for future dark theme support Currently using basic
 * dark colors - can be enhanced later
 */
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

/**
 * Custom typography for Empathy Training app Uses system fonts with
 * carefully chosen sizes for readability
 */
private val EmpathyTypography = Typography(
    // Display styles - for large headings
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp
    ), displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ), displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),

    // Headline styles - for section headings
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
    ),

    // Title styles - for card titles and important text
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ), titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ), titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),

    // Body styles - for main content text
    bodyLarge = TextStyle(
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
    ),

    // Label styles - for buttons and small text
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ), labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ), labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 14.sp
    )
)

/**
 * Main theme composable for Empathy Training app Automatically handles
 * light/dark theme and dynamic colors on Android 12+
 *
 * @param darkTheme Whether to use dark theme (follows system by default)
 * @param dynamicColor Whether to use dynamic colors on Android 12+
 *    (disabled for brand consistency)
 * @param content The app content to be themed
 */
@Composable
fun EmpathyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disabled to maintain brand colors
    content: @Composable () -> Unit,
) {
    // Choose color scheme based on theme and dynamic color settings
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Set system bar colors to match theme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    // Apply Material Theme with our custom colors and typography
    MaterialTheme(
        colorScheme = colorScheme, typography = EmpathyTypography, content = content
    )
}