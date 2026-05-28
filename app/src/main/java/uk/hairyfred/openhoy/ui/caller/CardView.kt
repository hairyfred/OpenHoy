package uk.hairyfred.openhoy.ui.caller

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.hairyfred.openhoy.model.Card
import uk.hairyfred.openhoy.ui.theme.CardWhite

/**
 * The big card filling most of the screen on the caller display.
 *
 * Layout for every card:
 *   ┌────────────────────┐
 *   │ rank + suit corner │
 *   │                    │
 *   │   CENTRE ICON      │  ← suit icon for number cards;
 *   │                    │     face icon (crown / heart-crown / jester
 *   │                    │     hat) for J / Q / K
 *   │   SUIT NAME        │
 *   │                    │
 *   │ rank + suit corner │
 *   └────────────────────┘
 *
 * Face cards swap the centre suit icon for a face icon. The suit is still
 * conveyed by the corner suit icons, the tint of the face icon, the suit
 * name, and the spoken TTS — so dropping the big centre suit icon costs us
 * nothing while making J/Q/K instantly distinguishable from number cards.
 */
@Composable
fun BigCardView(
    card: Card,
    fourColourDeck: Boolean,
    showSuitName: Boolean,
    modifier: Modifier = Modifier,
) {
    val tint = card.suit.colour(fourColourDeck)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(CardWhite)
            .border(4.dp, tint, RoundedCornerShape(28.dp))
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        // Top corner — rank in its own row so it never collides with content below.
        Row(modifier = Modifier.fillMaxWidth()) {
            CornerRank(card, tint)
            Spacer(modifier = Modifier.weight(1f))
        }

        // Centre — for number cards, a giant suit icon; for face cards, the
        // rank word ("QUEEN" / "KING" / "JACK") with the icon as a smaller
        // accent above. Symbols alone were unreliable for face cards, so the
        // word leads here — same reasoning as showing "HEARTS" alongside ♥.
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (card.rank.isFaceCard) {
                FaceCardIcon(
                    rank = card.rank,
                    tint = tint,
                    modifier = Modifier
                        .fillMaxWidth(0.32f)
                        .aspectRatio(1f),
                )
                AutoSizeText(
                    text = card.rank.spokenName.uppercase(),
                    color = tint,
                    maxFontSize = 72.sp,
                    sizingText = "QUEEN",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, start = 12.dp, end = 12.dp),
                )
            } else {
                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    // Largest square that fits the centre in BOTH dimensions, so
                    // tall icons (diamond/spade) can't clip on the short
                    // landscape card.
                    val side = minOf(maxWidth, maxHeight) * 0.9f
                    SuitIcon(
                        suit = card.suit,
                        tint = tint,
                        modifier = Modifier.size(side),
                    )
                }
            }
        }

        // Suit name on its own row. Auto-sizes down so even DIAMONDS fits the
        // narrower card in landscape without clipping.
        if (showSuitName) {
            AutoSizeText(
                text = card.suit.displayName,
                color = tint,
                maxFontSize = 44.sp,
                sizingText = "SPADES",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
            )
        }

        // Bottom corner — rank mirrored to the right.
        Row(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.weight(1f))
            CornerRank(card, tint)
        }
    }
}

/**
 * A single-line bold, centred text sized for visual consistency across a group
 * of cards:
 *  1. The font size is the largest (up to [maxFontSize]) at which [sizingText]
 *     fits the width — so common-length words render big.
 *  2. If the actual [text] is longer and would overflow at that size, it is
 *     condensed horizontally (scaleX) to fit, keeping the same letter HEIGHT.
 *
 * Result: every word has the same height (looks the same size); only an unusually
 * long word like DIAMONDS gets slightly narrower letters — instead of everything
 * shrinking down to DIAMONDS' size.
 */
@Composable
private fun AutoSizeText(
    text: String,
    color: Color,
    maxFontSize: TextUnit,
    modifier: Modifier = Modifier,
    sizingText: String = text,
) {
    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        val measurer = rememberTextMeasurer()
        val maxWidthPx = with(LocalDensity.current) { maxWidth.toPx() }

        fun widthOf(s: String, size: TextUnit) = measurer.measure(
            text = s,
            style = TextStyle(fontSize = size, fontWeight = FontWeight.Black),
            maxLines = 1,
            softWrap = false,
        ).size.width

        val fontSize = remember(sizingText, maxFontSize, maxWidthPx) {
            var size = maxFontSize
            while (size.value > 14f && widthOf(sizingText, size) > maxWidthPx) {
                size = size * 0.95f
            }
            size
        }
        val condense = remember(text, fontSize, maxWidthPx) {
            val w = widthOf(text, fontSize)
            if (w > maxWidthPx && w > 0) (maxWidthPx / w).coerceIn(0.4f, 1f) else 1f
        }
        // Lay the text out unbounded (full width) and condense it horizontally at
        // draw time; this avoids the text being clipped to the box width before
        // the scale is applied.
        Text(
            text = text,
            color = color,
            fontWeight = FontWeight.Black,
            fontSize = fontSize,
            maxLines = 1,
            softWrap = false,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Visible,
            modifier = Modifier
                .wrapContentWidth(unbounded = true)
                .graphicsLayer {
                    scaleX = condense
                    transformOrigin = TransformOrigin(0.5f, 0.5f)
                },
        )
    }
}

@Composable
private fun CornerRank(card: Card, tint: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = card.rank.symbol,
            color = tint,
            fontWeight = FontWeight.Black,
            fontSize = 48.sp,
        )
        SuitIcon(
            suit = card.suit,
            tint = tint,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(36.dp),
        )
    }
}

/** Small placeholder card for use in the history strip. */
@Composable
fun MiniCardView(
    card: Card,
    fourColourDeck: Boolean,
    modifier: Modifier = Modifier,
) {
    val tint = card.suit.colour(fourColourDeck)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CardWhite)
            .border(2.dp, tint, RoundedCornerShape(12.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = card.rank.symbol,
                color = tint,
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.titleLarge,
            )
            SuitIcon(
                suit = card.suit,
                tint = tint,
                modifier = Modifier.size(32.dp),
            )
        }
    }
}
