package uk.hairyfred.openhoy.ui.caller

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import uk.hairyfred.openhoy.model.Card

/**
 * Landscape history: a scrolling grid of previously called cards, most recent
 * first. When a new card lands it scales in at the top-left and the rest of the
 * grid slides down into place (animateItemPlacement).
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistoryGrid(
    history: List<Card>,
    fourColourDeck: Boolean,
    onCardTap: (Card) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Recent calls",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
        )
        val state = rememberLazyGridState()
        LaunchedEffect(history.firstOrNull()) {
            if (history.isNotEmpty()) state.animateScrollToItem(0)
        }
        LazyVerticalGrid(
            state = state,
            columns = GridCells.Adaptive(minSize = 76.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(4.dp),
        ) {
            items(history, key = { it.historyKey }) { card ->
                AnimatedMiniCard(
                    card = card,
                    fourColourDeck = fourColourDeck,
                    onClick = { onCardTap(card) },
                    modifier = Modifier.animateItemPlacement(tween(durationMillis = 320)),
                )
            }
        }
    }
}

@Composable
private fun AnimatedMiniCard(
    card: Card,
    fourColourDeck: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Fresh keys (newly called cards) start small + transparent and pop in.
    // Existing items keep their state, so only the new card animates.
    val appear = remember { Animatable(0.6f) }
    LaunchedEffect(Unit) { appear.animateTo(1f, tween(durationMillis = 260)) }
    MiniCardView(
        card = card,
        fourColourDeck = fourColourDeck,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.74f)
            .graphicsLayer {
                val s = appear.value
                scaleX = s
                scaleY = s
                alpha = ((s - 0.6f) / 0.4f).coerceIn(0f, 1f)
            }
            .clickable(onClick = onClick),
    )
}
