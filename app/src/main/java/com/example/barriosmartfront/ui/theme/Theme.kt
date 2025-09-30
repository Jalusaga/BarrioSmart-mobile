// ui/theme/Theme.kt
package com.example.barriosmartfront.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CornerSize

private val ColorScheme = lightColorScheme(
    primary = PrimaryTeal,
    onPrimary = OnPrimary,
    secondary = SecondaryAmber,
    surface = SurfaceSoft,
    onSurface = OnSurfaceHigh,
    outline = OutlineSoft
)

@Composable
fun SeguridadTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ColorScheme,
        typography = Typography(),
        shapes = Shapes(
            extraSmall = ShapeDefaults.ExtraSmall.copy(all = CornerSize(8.dp)),
            small      = ShapeDefaults.Small.copy(all = CornerSize(12.dp)),
            medium     = ShapeDefaults.Medium.copy(all = CornerSize(16.dp)),
            large      = ShapeDefaults.Large.copy(all = CornerSize(24.dp))
        ),
        content = content
    )
}
