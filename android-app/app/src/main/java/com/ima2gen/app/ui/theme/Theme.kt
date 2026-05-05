package com.ima2gen.app.ui.theme

// Main application theme definition
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// Explicit imports for colors in the same package to help the compiler
import com.ima2gen.app.ui.theme.Primary
import com.ima2gen.app.ui.theme.PrimaryDark
import com.ima2gen.app.ui.theme.PrimaryLight
import com.ima2gen.app.ui.theme.Secondary
import com.ima2gen.app.ui.theme.SecondaryDark
import com.ima2gen.app.ui.theme.SecondaryLight
import com.ima2gen.app.ui.theme.SurfaceDark
import com.ima2gen.app.ui.theme.SurfaceContainerDark
import com.ima2gen.app.ui.theme.SurfaceVariantDark
import com.ima2gen.app.ui.theme.OnSurfaceDark
import com.ima2gen.app.ui.theme.SurfaceLight
import com.ima2gen.app.ui.theme.SurfaceContainerLight
import com.ima2gen.app.ui.theme.SurfaceVariantLight
import com.ima2gen.app.ui.theme.OnSurfaceLight
import com.ima2gen.app.ui.theme.Error

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = SurfaceDark,
    primaryContainer = PrimaryDark,
    secondary = Secondary,
    onSecondary = SurfaceDark,
    secondaryContainer = SecondaryDark,
    surface = SurfaceDark,
    surfaceContainer = SurfaceContainerDark,
    surfaceVariant = SurfaceVariantDark,
    onSurface = OnSurfaceDark,
    error = Error,
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryDark,
    onPrimary = SurfaceLight,
    primaryContainer = PrimaryLight,
    secondary = SecondaryDark,
    onSecondary = SurfaceLight,
    secondaryContainer = SecondaryLight,
    surface = SurfaceLight,
    surfaceContainer = SurfaceContainerLight,
    surfaceVariant = SurfaceVariantLight,
    onSurface = OnSurfaceLight,
    error = Error,
)

@Composable
fun Ima2GenTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
