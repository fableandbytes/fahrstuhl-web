package com.fableandbytes.fahrstuhl.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val DarkColorScheme = darkColorScheme(
    primary = SoftGold,
    secondary = WoodBrown,
    tertiary = ArtDecoGreen,
    background = WoodDark,
    surface = WoodDark,
    onPrimary = WoodDark,
    onSecondary = Cream,
    onTertiary = Cream,
    onBackground = Cream,
    onSurface = Cream
)

val LightColorScheme = lightColorScheme(
    primary = WoodBrown,
    secondary = SoftGold,
    tertiary = ArtDecoGreen,
    background = Cream,
    surface = Cream,
    onPrimary = Color.White,
    onSecondary = WoodDark,
    onTertiary = Color.White,
    onBackground = WoodDark,
    onSurface = WoodDark
)

@Composable
expect fun FahrstuhlKartenspielTheme(
    darkTheme: Boolean = androidx.compose.foundation.isSystemInDarkTheme(),
    dynamicColor: Boolean = true, // Nur für Android relevant
    content: @Composable () -> Unit
)
