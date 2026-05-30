package uk.hairyfred.openhoy.ui.caller

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

/**
 * A five-pointed star — point up — used as the visual marker for the Joker
 * card. Drawn from a Path so it scales crisply and recolours via [tint].
 *
 * Normalised to a 100x100 box.
 */
@Composable
fun StarIcon(tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        fun x(v: Float) = v / 100f * w
        fun y(v: Float) = v / 100f * h

        // Outer radius 38, inner radius 16, centre (50,50). Vertices alternate
        // outer/inner going clockwise from the top.
        val path = Path().apply {
            moveTo(x(50f), y(12f))      // top outer
            lineTo(x(59.4f), y(37.1f))  // upper-right inner
            lineTo(x(86.1f), y(38.3f))  // right outer
            lineTo(x(65.2f), y(54.9f))  // right inner
            lineTo(x(72.3f), y(80.7f))  // lower-right outer
            lineTo(x(50f), y(66f))      // bottom inner
            lineTo(x(27.7f), y(80.7f))  // lower-left outer
            lineTo(x(34.8f), y(54.9f))  // left inner
            lineTo(x(13.9f), y(38.3f))  // left outer
            lineTo(x(40.6f), y(37.1f))  // upper-left inner
            close()
        }
        drawPath(path, tint)
    }
}
