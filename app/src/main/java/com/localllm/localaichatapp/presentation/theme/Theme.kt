package com.localllm.localaichatapp.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SecondaryContainerLight,
    onSecondaryContainer = OnSecondaryContainerLight,
    tertiary = TertiaryLight,
    onTertiary = OnTertiaryLight,
    tertiaryContainer = TertiaryContainerLight,
    onTertiaryContainer = OnTertiaryContainerLight,
    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight,
    outlineVariant = OutlineVariantLight
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = TertiaryContainerDark,
    onTertiaryContainer = OnTertiaryContainerDark,
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark
)

// Custom colors that don't fit into Material 3 color scheme
data class ExtendedColors(
    val aiBlue: Color,
    val aiBlueVariant: Color,
    val aiGreen: Color,
    val aiRed: Color,
    val aiYellow: Color,
    val aiPurple: Color,
    val chatColor: Color,
    val imageColor: Color,
    val audioColor: Color,
    val promptLabColor: Color,
    val successColor: Color,
    val warningColor: Color,
    val errorColor: Color,
    val infoColor: Color,
    val excellentPerformance: Color,
    val goodPerformance: Color,
    val averagePerformance: Color,
    val poorPerformance: Color,
    val badPerformance: Color
)

private val LightExtendedColors = ExtendedColors(
    aiBlue = AiBlue,
    aiBlueVariant = AiBlueVariant,
    aiGreen = AiGreen,
    aiRed = AiRed,
    aiYellow = AiYellow,
    aiPurple = AiPurple,
    chatColor = ChatColor,
    imageColor = ImageColor,
    audioColor = AudioColor,
    promptLabColor = PromptLabColor,
    successColor = SuccessColor,
    warningColor = WarningColor,
    errorColor = ErrorColor,
    infoColor = InfoColor,
    excellentPerformance = ExcellentPerformance,
    goodPerformance = GoodPerformance,
    averagePerformance = AveragePerformance,
    poorPerformance = PoorPerformance,
    badPerformance = BadPerformance
)

private val DarkExtendedColors = ExtendedColors(
    aiBlue = AiBlue,
    aiBlueVariant = AiBlueVariant,
    aiGreen = AiGreen,
    aiRed = AiRed,
    aiYellow = AiYellow,
    aiPurple = AiPurple,
    chatColor = ChatColor,
    imageColor = ImageColor,
    audioColor = AudioColor,
    promptLabColor = PromptLabColor,
    successColor = SuccessColor,
    warningColor = WarningColor,
    errorColor = ErrorColor,
    infoColor = InfoColor,
    excellentPerformance = ExcellentPerformance,
    goodPerformance = GoodPerformance,
    averagePerformance = AveragePerformance,
    poorPerformance = PoorPerformance,
    badPerformance = BadPerformance
)

val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }

@Composable
fun LocalAiChatAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    CompositionLocalProvider(
        LocalExtendedColors provides extendedColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

// Extension property to access extended colors
val MaterialTheme.extendedColors: ExtendedColors
    @Composable
    get() = LocalExtendedColors.current