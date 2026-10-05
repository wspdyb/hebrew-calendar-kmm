package wspdyb.calculator.zmanim.ui.theme

import androidx.compose.foundation.shape.RectangleShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Rectangular, no-rounded classical shapes
val ZmanimShapes = Shapes(
    // No rounding - pure rectangles
    small = RectangleShape,
    medium = RectangleShape,
    large = RectangleShape
)

// For explicit rectangle use
val RectangleShape0 = RectangleShape
val RectangleShape1dp = RectangleShape  // Still rectangle, but visually distinct with borders
