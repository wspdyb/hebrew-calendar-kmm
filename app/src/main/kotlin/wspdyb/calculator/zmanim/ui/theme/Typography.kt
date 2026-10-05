package wspdyb.calculator.zmanim.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import wspdyb.calculator.zmanim.R

// Font Families
val RobotoFamily = FontFamily(
    Font(R.font.roboto_regular, FontWeight.Normal),
    Font(R.font.roboto_medium, FontWeight.Medium),
    Font(R.font.roboto_bold, FontWeight.Bold)
)

val JetBrainsMonoFamily = FontFamily(
    Font(R.font.jetbrains_mono_regular, FontWeight.Normal),
    Font(R.font.jetbrains_mono_medium, FontWeight.Medium)
)

// Typography System
val ZmanimTypography = Typography(
    // Display styles (large headlines)
    displayLarge = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    displayMedium = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),

    // Headline styles
    headlineLarge = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp
    ),

    // Title styles
    titleLarge = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleMedium = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    titleSmall = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp
    ),

    // Body styles
    bodyLarge = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),

    // Label styles
    labelLarge = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

// Special styles for calculation display
val CalculationNumberStyle = TextStyle(
    fontFamily = JetBrainsMonoFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.sp
)

val CalculationLabelStyle = TextStyle(
    fontFamily = RobotoFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.sp
)
