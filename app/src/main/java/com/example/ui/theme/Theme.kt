package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = JagoTeal,
    secondary = JagoPurple,
    tertiary = JagoGold,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = SurfaceCard,
    onSecondary = SurfaceCard,
    onBackground = SurfaceCard,
    onSurface = SurfaceCard,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = JagoTeal,
    secondary = JagoPurple,
    tertiary = JagoGold,
    background = LightBackground,
    surface = SurfaceCard,
    onPrimary = SurfaceCard,
    onSecondary = SurfaceCard,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disable dynamicColor by default to enforce Jago Modal branding colors
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
