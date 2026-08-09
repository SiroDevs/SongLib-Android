package com.songlib.core.design_system.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightTheme = lightColorScheme(
    primary = LightColors.primary,
    onPrimary = LightColors.onPrimary,
    primaryContainer = LightColors.primaryContainer,
    onPrimaryContainer = LightColors.onPrimaryContainer,
    secondary = LightColors.secondary,
    onSecondary = LightColors.onSecondary,
    secondaryContainer = LightColors.secondaryContainer,
    onSecondaryContainer = LightColors.onSecondaryContainer,
    tertiary = LightColors.tertiary,
    onTertiary = LightColors.onTertiary,
    tertiaryContainer = LightColors.tertiaryContainer,
    onTertiaryContainer = LightColors.onTertiaryContainer,
    error = LightColors.error,
    errorContainer = LightColors.errorContainer,
    onError = LightColors.onError,
    onErrorContainer = LightColors.onErrorContainer,
    background = LightColors.background,
    onBackground = LightColors.onBackground,
    surface = LightColors.surface,
    onSurface = LightColors.onSurface,
    surfaceVariant = LightColors.surfaceVariant,
    onSurfaceVariant = LightColors.onSurfaceVariant,
    outline = LightColors.outline,
    inverseOnSurface = LightColors.inverseOnSurface,
    inverseSurface = LightColors.inverseSurface,
    inversePrimary = LightColors.inversePrimary,
    surfaceTint = LightColors.surfaceTint,
    outlineVariant = LightColors.outlineVariant,
    scrim = LightColors.scrim,
)

private val DarkTheme = darkColorScheme(
    primary = DarkColors.primary,
    onPrimary = DarkColors.onPrimary,
    primaryContainer = DarkColors.primaryContainer,
    onPrimaryContainer = DarkColors.onPrimaryContainer,
    secondary = DarkColors.secondary,
    onSecondary = DarkColors.onSecondary,
    secondaryContainer = DarkColors.secondaryContainer,
    onSecondaryContainer = DarkColors.onSecondaryContainer,
    tertiary = DarkColors.tertiary,
    onTertiary = DarkColors.onTertiary,
    tertiaryContainer = DarkColors.tertiaryContainer,
    onTertiaryContainer = DarkColors.onTertiaryContainer,
    error = DarkColors.error,
    errorContainer = DarkColors.errorContainer,
    onError = DarkColors.onError,
    onErrorContainer = DarkColors.onErrorContainer,
    background = DarkColors.background,
    onBackground = DarkColors.onBackground,
    surface = DarkColors.surface,
    onSurface = DarkColors.onSurface,
    surfaceVariant = DarkColors.surfaceVariant,
    onSurfaceVariant = DarkColors.onSurfaceVariant,
    outline = DarkColors.outline,
    inverseOnSurface = DarkColors.inverseOnSurface,
    inverseSurface = DarkColors.inverseSurface,
    inversePrimary = DarkColors.inversePrimary,
    surfaceTint = DarkColors.surfaceTint,
    outlineVariant = DarkColors.outlineVariant,
    scrim = DarkColors.scrim,
)

@Composable
fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        useDarkTheme -> DarkTheme
        else -> LightTheme
    }

    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            // Guard: during the SplashScreen transition the view may not yet be
            // attached to an Activity window — skip safely rather than crashing.
            val activity = view.context as? Activity ?: return@SideEffect
            val window = activity.window

            // Let Compose draw edge-to-edge behind the system bars.
            WindowCompat.setDecorFitsSystemWindows(window, false)

            // On API 35+ (VANILLA_ICE_CREAM) the platform enforces edge-to-edge and
            // ignores statusBarColor, so we only set the icon tint.
            // On API 26-34 make the bar transparent; Compose content paints behind it.
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                @Suppress("DEPRECATION")
                window.statusBarColor = android.graphics.Color.TRANSPARENT
            }

            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !useDarkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
        shapes = shapes,
    )
}
