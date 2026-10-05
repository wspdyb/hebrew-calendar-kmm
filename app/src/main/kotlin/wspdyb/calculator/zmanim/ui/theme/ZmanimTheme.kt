package wspdyb.calculator.zmanim.ui.theme

import androidx.compose.foundation.isSystemInDarkMode
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Classical Professional Palette
private val PrimaryBlue = Color(0xFF1A3A52)
private val PrimaryDarkBlue = Color(0xFF0F2538)
private val PrimaryLightBlue = Color(0xFF284A6D)

private val SecondaryRust = Color(0xFFC65D3B)
private val SecondaryDarkRust = Color(0xFFA64D2F)
private val SecondaryLightRust = Color(0xFFD97550)

private val AccentGold = Color(0xFFD4AF37)
private val AccentDarkGold = Color(0xFFB8860B)

private val SurfaceLight = Color(0xFFF5F5F5)
private val SurfaceDark = Color(0xFF1F1F1F)
private val BackgroundWhite = Color(0xFFFFFFFF)
private val BackgroundDark = Color(0xFF121212)

private val TextPrimary = Color(0xFF333333)
private val TextSecondary = Color(0xFF666666)
private val TextOnPrimary = Color(0xFFFFFFFF)

private val ErrorRed = Color(0xFFC62828)
private val SuccessGreen = Color(0xFF2E7D32)
private val WarningOrange = Color(0xFFF57C00)

// Light Color Scheme
private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = TextOnPrimary,
    primaryContainer = PrimaryLightBlue,
    onPrimaryContainer = TextPrimary,
    
    secondary = SecondaryRust,
    onSecondary = TextOnPrimary,
    secondaryContainer = SecondaryLightRust,
    onSecondaryContainer = TextPrimary,
    
    tertiary = AccentGold,
    onTertiary = TextPrimary,
    tertiaryContainer = AccentGold,
    onTertiaryContainer = TextPrimary,
    
    error = ErrorRed,
    onError = TextOnPrimary,
    errorContainer = Color(0xFFFFB4AB),
    onErrorContainer = ErrorRed,
    
    background = BackgroundWhite,
    onBackground = TextPrimary,
    
    surface = SurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFFE0E0E0),
    onSurfaceVariant = TextSecondary,
    
    outline = Color(0xFFCCCCCC),
    outlineVariant = Color(0xFFBBBBBB),
    
    scrim = Color(0x00000040)
)

// Dark Color Scheme
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryLightBlue,
    onPrimary = TextPrimary,
    primaryContainer = PrimaryBlue,
    onPrimaryContainer = TextOnPrimary,
    
    secondary = SecondaryLightRust,
    onSecondary = TextPrimary,
    secondaryContainer = SecondaryRust,
    onSecondaryContainer = TextOnPrimary,
    
    tertiary = AccentGold,
    onTertiary = TextPrimary,
    tertiaryContainer = AccentDarkGold,
    onTertiaryContainer = TextOnPrimary,
    
    error = Color(0xFFFFB4AB),
    onError = ErrorRed,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFB4AB),
    
    background = BackgroundDark,
    onBackground = Color(0xFFFFFFFF),
    
    surface = SurfaceDark,
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFF49454E),
    onSurfaceVariant = Color(0xFFCAC7D0),
    
    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFF49454E),
    
    scrim = Color(0x00000040)
)

@Composable
fun ZmanimTheme(
    useDarkTheme: Boolean = isSystemInDarkMode(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        useDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ZmanimTypography,
        shapes = ZmanimShapes,
        content = content
    )
}
