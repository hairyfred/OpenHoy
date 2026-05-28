package uk.hairyfred.openhoy.ui.caller

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import uk.hairyfred.openhoy.model.Suit

/**
 * Suit icons are hand-drawn here (not Unicode glyphs) so we can exaggerate the
 * shape differences between clubs and spades — the whole point of this app.
 *
 * Each icon is normalised into a 100x100 box and scaled to the canvas size.
 */
@Composable
fun SuitIcon(suit: Suit, tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        fun x(v: Float) = v / 100f * w
        fun y(v: Float) = v / 100f * h

        val path = when (suit) {
            Suit.SPADES -> spadePath(::x, ::y)
            Suit.HEARTS -> heartPath(::x, ::y)
            Suit.DIAMONDS -> diamondPath(::x, ::y)
            Suit.CLUBS -> clubPath(::x, ::y)
        }
        drawPath(path, tint)
    }
}

/** Spade: sharp top, concave flares, tall narrow stem. */
private fun spadePath(x: (Float) -> Float, y: (Float) -> Float): Path = Path().apply {
    // Body of the spade — sharp tip at the top.
    moveTo(x(50f), y(8f))
    cubicTo(x(50f), y(8f), x(95f), y(45f), x(86f), y(64f))
    cubicTo(x(78f), y(80f), x(60f), y(78f), x(55f), y(68f))
    // little inward dip into the stem
    cubicTo(x(54f), y(74f), x(56f), y(80f), x(60f), y(86f))
    // Bottom of stem
    lineTo(x(40f), y(86f))
    cubicTo(x(44f), y(80f), x(46f), y(74f), x(45f), y(68f))
    // Mirror — left flank back up to tip
    cubicTo(x(40f), y(78f), x(22f), y(80f), x(14f), y(64f))
    cubicTo(x(5f), y(45f), x(50f), y(8f), x(50f), y(8f))
    close()
}

/** Heart: two upper lobes meeting in a centre dip, single bottom point. */
private fun heartPath(x: (Float) -> Float, y: (Float) -> Float): Path = Path().apply {
    moveTo(x(50f), y(88f))
    // Left side from bottom point up around the left lobe and into the centre dip
    cubicTo(x(0f), y(60f), x(0f), y(20f), x(28f), y(20f))
    cubicTo(x(40f), y(20f), x(50f), y(30f), x(50f), y(38f))
    // Right side from centre dip out around the right lobe and back to bottom point
    cubicTo(x(50f), y(30f), x(60f), y(20f), x(72f), y(20f))
    cubicTo(x(100f), y(20f), x(100f), y(60f), x(50f), y(88f))
    close()
}

/** Diamond: rhombus, slightly elongated vertically. */
private fun diamondPath(x: (Float) -> Float, y: (Float) -> Float): Path = Path().apply {
    moveTo(x(50f), y(6f))
    lineTo(x(88f), y(50f))
    lineTo(x(50f), y(94f))
    lineTo(x(12f), y(50f))
    close()
}

/**
 * Club: three full circles in a trefoil arrangement + short stubby stem.
 * Circles are drawn explicitly so they read as round at any size — the round
 * lobes are what visually distinguish a club from a spade for users with LD.
 */
private fun clubPath(x: (Float) -> Float, y: (Float) -> Float): Path = Path().apply {
    val topCx = 50f; val topCy = 28f
    val leftCx = 26f; val leftCy = 56f
    val rightCx = 74f; val rightCy = 56f
    val r = 20f

    addCircle(x, y, topCx, topCy, r)
    addCircle(x, y, leftCx, leftCy, r)
    addCircle(x, y, rightCx, rightCy, r)

    // Stubby stem at the bottom — kept short and wide so it does not look pointed.
    moveTo(x(42f), y(70f))
    lineTo(x(58f), y(70f))
    lineTo(x(64f), y(90f))
    lineTo(x(36f), y(90f))
    close()
}

private fun Path.addCircle(
    x: (Float) -> Float,
    y: (Float) -> Float,
    cx: Float,
    cy: Float,
    r: Float,
) {
    // Approximate a circle using 4 cubic beziers (k = 0.5522847498).
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

