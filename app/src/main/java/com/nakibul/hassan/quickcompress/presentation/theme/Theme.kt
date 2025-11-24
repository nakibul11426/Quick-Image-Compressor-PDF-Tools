package com.nakibul.hassan.quickcompress.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = PrimaryBlueLight,
    onPrimaryContainer = PrimaryBlueDark,
    
    secondary = SecondaryTeal,
    onSecondary = Color.White,
    secondaryContainer = SecondaryTealLight,
    onSecondaryContainer = Color(0xFF00363A),
    
    tertiary = AccentPurple,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFEDE7F6),
    onTertiaryContainer = Color(0xFF311B92),
    
    background = BackgroundLight,
    onBackground = TextPrimary,
    
    surface = SurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = TextSecondary,
    
    surfaceTint = PrimaryBlue,
    inverseSurface = Color(0xFF2E2E2E),
    inverseOnSurface = Color(0xFFF4F4F4),
    
    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorRedLight,
    onErrorContainer = Color(0xFF5F0000),
    
    outline = Color(0xFFBDBDBD),
    outlineVariant = Color(0xFFE0E0E0),
    scrim = ScrimLight
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlueLight,
    onPrimary = Color(0xFF003258),
    primaryContainer = PrimaryBlueDark,
    onPrimaryContainer = Color(0xFFD1E4FF),
    
    secondary = SecondaryTealLight,
    onSecondary = Color(0xFF00363A),
    secondaryContainer = Color(0xFF004F58),
    onSecondaryContainer = Color(0xFFB2EBF2),
    
    tertiary = Color(0xFFB39DDB),
    onTertiary = Color(0xFF311B92),
    tertiaryContainer = Color(0xFF4527A0),
    onTertiaryContainer = Color(0xFFD1C4E9),
    
    background = BackgroundDark,
    onBackground = Color(0xFFE6E6E6),
    
    surface = SurfaceDark,
    onSurface = Color(0xFFE6E6E6),
    surfaceVariant = SurfaceElevatedDark,
    onSurfaceVariant = Color(0xFFCDCDCD),
    
    surfaceTint = PrimaryBlueLight,
    inverseSurface = Color(0xFFE6E6E6),
    inverseOnSurface = Color(0xFF1A1A1A),
    
    error = ErrorRedLight,
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    
    outline = Color(0xFF8E8E8E),
    outlineVariant = Color(0xFF444444),
    scrim = ScrimDark
)

@Composable
fun QuickCompressTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
