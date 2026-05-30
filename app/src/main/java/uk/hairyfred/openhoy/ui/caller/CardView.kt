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
import uk.hairyfred.openhoy.model.Joker
import uk.hairyfred.openhoy.model.JokerVariant
import uk.hairyfred.openhoy.model.SuitedCard
import uk.hairyfred.openhoy.ui.theme.CardWhite

/**
 * Joker tints. The plain joker uses an orange that isn't any of the four suit
 * colours, so a "plain" joker is unmistakable. In 2-colour mode the jokers
 * pick up the same red / black as the hearts / spades suits, matching the
 * "red joker" / "black joker" naming convention.
 */
private fun JokerVariant.tint(): Color = when (this) {
    JokerVariant.PLAIN -> Color(0xFFE65100)
    JokerVariant.RED -> Color(0xFFD32F2F)
    JokerVariant.BLACK -> Color(0xFF111111)
}

/**
 * The big card filling most of the screen on the caller display.
 *
 * Dispatches by card type: number/face cards use the suited layout (corner
 * rank, big centre suit/face, suit name); jokers get their own simpler layout
 * (big star + JOKER word, no suit).
 */
@Composable
fun BigCardView(
    card: Card,
    fourColourDeck: Boolean,
    showSuitName: Boolean,
    modifier: Modifier = Modifier,
) {
    when (card) {
        is SuitedCard -> SuitedBigCard(card, fourColourDeck, showSuitName, modifier)
        is Joker -> JokerBigCard(card, modifier)
    }
}

@Composable
private fun SuitedBigCard(
    card: SuitedCard,
    fourColourDeck: Boolean,
    showSuitName: Boolean,
    modifier: Modifier,
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
        Row(modifier = Modifier.fillMaxWidth()) {
            CornerRank(card, tint)
            Spacer(modifier = Modifier.weight(1f))
        }

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
                    val side = minOf(maxWidth, maxHeight) * 0.9f
                    SuitIcon(
                        suit = card.suit,
                        tint = tint,
                        modifier = Modifier.size(side),
                    )
                }
            }
        }

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

        Row(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.weight(1f))
            CornerRank(card, tint)
        }
    }
}

@Composable
private fun JokerBigCard(card: Joker, modifier: Modifier) {
    val tint = card.variant.tint()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(CardWhite)
            .border(4.dp, tint, RoundedCornerShape(28.dp))
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            CornerStar(tint)
            Spacer(modifier = Modifier.weight(1f))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            StarIcon(
                tint = tint,
                modifier = Modifier
                    .fillMaxWidth(0.36f)
                    .aspectRatio(1f),
            )
            AutoSizeText(
                text = "JOKER",
                color = tint,
                maxFontSize = 72.sp,
                sizingText = "QUEEN",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, start = 12.dp, end = 12.dp),
            )
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.weight(1f))
            CornerStar(tint)
        }
    }
}

@Composable
private fun CornerRank(card: SuitedCard, tint: Color, modifier: Modifier = Modifier) {
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

@Composable
private fun CornerStar(tint: Color, modifier: Modifier = Modifier) {
    StarIcon(
        tint = tint,
        modifier = modifier.size(56.dp),
    )
}

/** Small placeholder card for use in the history strip / grid. */
@Composable
fun MiniCardView(
    card: Card,
    fourColourDeck: Boolean,
    modifier: Modifier = Modifier,
) {
    when (card) {
        is SuitedCard -> SuitedMiniCard(card, fourColourDeck, modifier)
        is Joker -> JokerMiniCard(card, modifier)
    }
}

@Composable
private fun SuitedMiniCard(card: SuitedCard, fourColourDeck: Boolean, modifier: Modifier) {
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

@Composable
private fun JokerMiniCard(card: Joker, modifier: Modifier) {
    val tint = card.variant.tint()
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
                text = "JKR",
                color = tint,
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.titleLarge,
            )
            StarIcon(
                tint = tint,
                modifier = Modifier.size(32.dp),
            )
        }
    }
}

/**
 * Single-line bold centred text sized for visual consistency across a group of
 * cards. The font size is chosen so [sizingText] fits the width; if the actual
 * [text] is longer it is condensed horizontally (scaleX) rather than shrunk —
 * so every word looks the same height, only an unusually long one (DIAMONDS)
 * gets slightly narrower letters.
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
