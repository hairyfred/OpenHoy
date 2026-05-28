package uk.hairyfred.openhoy.ui.caller

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import uk.hairyfred.openhoy.model.Rank

/**
 * Face-card icons drawn as Compose Paths so they scale crisply at any size and
 * recolour with the suit tint.
 *
 * Each icon picks a single unambiguous symbol:
 *   King   -> tall three-prong crown with jewels — pointed spikes
 *   Queen  -> tiara with five small spikes topped with pearls — clearly NOT
 *             the heart suit (an earlier heart-based design conflicted with
 *             the hearts suit and confused clients).
 *   Jack   -> jester hat with two floppy points and bells
 *
 * Normalised to a 100x100 box.
 */
@Composable
fun FaceCardIcon(rank: Rank, tint: Color, modifier: Modifier = Modifier) {
    if (!rank.isFaceCard) return
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        fun x(v: Float) = v / 100f * w
        fun y(v: Float) = v / 100f * h

        val pieces: List<Path> = when (rank) {
            Rank.KING -> kingPieces(::x, ::y)
            Rank.QUEEN -> queenPieces(::x, ::y)
            Rank.JACK -> jackPieces(::x, ::y)
            else -> return@Canvas
        }
        pieces.forEach { drawPath(it, tint) }
    }
}

// ---------------------------------------------------------------------------
// KING — three-prong crown
// ---------------------------------------------------------------------------

private fun kingPieces(x: (Float) -> Float, y: (Float) -> Float): List<Path> {
    val crown = Path().apply {
        // Wide base.
        moveTo(x(10f), y(78f))
        lineTo(x(90f), y(78f))
        // Right side rising up to the right spike.
        lineTo(x(90f), y(54f))
        lineTo(x(78f), y(22f))      // right spike tip
        lineTo(x(63f), y(50f))      // dip
        // Centre spike — tallest.
        lineTo(x(50f), y(8f))
        lineTo(x(37f), y(50f))      // dip
        // Left spike and back down to the base.
        lineTo(x(22f), y(22f))      // left spike tip
        lineTo(x(10f), y(54f))
        close()
    }
    return buildList {
        add(crown)
        add(circle(x, y, cx = 22f, cy = 18f, r = 5f))   // left jewel
        add(circle(x, y, cx = 50f, cy = 4f, r = 6f))    // centre jewel (sits on tallest spike)
        add(circle(x, y, cx = 78f, cy = 18f, r = 5f))   // right jewel
        add(circle(x, y, cx = 50f, cy = 66f, r = 6f))   // big band gem
    }
}

// ---------------------------------------------------------------------------
// QUEEN — heart on a crown band
// ---------------------------------------------------------------------------

private fun queenPieces(x: (Float) -> Float, y: (Float) -> Float): List<Path> {
    // Tiara — five spikes, ascending in height toward the centre, each
    // topped by a pearl (circle). Distinct from the king's three pointed
    // prongs by being more numerous, shorter and pearl-tipped.
    val tiara = Path().apply {
        moveTo(x(8f), y(78f))       // base bottom-left
        lineTo(x(92f), y(78f))      // base bottom-right
        lineTo(x(92f), y(58f))      // right side up to band top

        // Right small spike.
        lineTo(x(85f), y(42f))
        lineTo(x(76f), y(58f))      // dip
        // Right mid spike.
        lineTo(x(68f), y(32f))
        lineTo(x(58f), y(58f))      // dip
        // Centre spike — tallest.
        lineTo(x(50f), y(20f))
        lineTo(x(42f), y(58f))      // dip
        // Left mid spike.
        lineTo(x(32f), y(32f))
        lineTo(x(24f), y(58f))      // dip
        // Left small spike.
        lineTo(x(15f), y(42f))
        lineTo(x(8f), y(58f))       // left side down to band
        close()
    }
    return buildList {
        add(tiara)
        // Pearls on top of each spike tip.
        add(circle(x, y, cx = 15f, cy = 38f, r = 5f))
        add(circle(x, y, cx = 32f, cy = 28f, r = 5f))
        add(circle(x, y, cx = 50f, cy = 14f, r = 6f))   // centre, slightly larger
        add(circle(x, y, cx = 68f, cy = 28f, r = 5f))
        add(circle(x, y, cx = 85f, cy = 38f, r = 5f))
        // A single band gem to balance the design.
        add(circle(x, y, cx = 50f, cy = 70f, r = 5f))
    }
}

// ---------------------------------------------------------------------------
// JACK — jester hat with bells
// ---------------------------------------------------------------------------

private fun jackPieces(x: (Float) -> Float, y: (Float) -> Float): List<Path> {
    val hat = Path().apply {
        moveTo(x(10f), y(70f))
        // Brim curves slightly upward in the middle.
        cubicTo(x(30f), y(78f), x(70f), y(78f), x(90f), y(70f))
        // Right point flopping outward and down to a tip.
        cubicTo(x(98f), y(60f), x(95f), y(40f), x(80f), y(22f))
        // Inward dip toward the centre.
        cubicTo(x(70f), y(40f), x(60f), y(45f), x(50f), y(45f))
        // Left point flopping outward to its tip.
        cubicTo(x(40f), y(45f), x(30f), y(40f), x(20f), y(22f))
        cubicTo(x(5f), y(40f), x(2f), y(60f), x(10f), y(70f))
        close()
    }
    return buildList {
        add(hat)
        add(circle(x, y, cx = 20f, cy = 18f, r = 5f))   // left bell
        add(circle(x, y, cx = 80f, cy = 18f, r = 5f))   // right bell
    }
}

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

/** Filled circle as a Path, using four cubic beziers (k = 0.5522847498). */
private fun circle(
    x: (Float) -> Float,
    y: (Float) -> Float,
    cx: Float,
    cy: Float,
    r: Float,
): Path = Path().apply {
    val k = 0.5522847498f
    moveTo(x(cx), y(cy - r))
    cubicTo(
        x(cx + r * k), y(cy - r),
        x(cx + r), y(cy - r * k),
        x(cx + r), y(cy),
    )
    cubicTo(
        x(cx + r), y(cy + r * k),
        x(cx + r * k), y(cy + r),
        x(cx), y(cy + r),
    )
    cubicTo(
        x(cx - r * k), y(cy + r),
        x(cx - r), y(cy + r * k),
        x(cx - r), y(cy),
    )
    cubicTo(
        x(cx - r), y(cy - r * k),
        x(cx - r * k), y(cy - r),
        x(cx), y(cy - r),
    )
    close()
}
