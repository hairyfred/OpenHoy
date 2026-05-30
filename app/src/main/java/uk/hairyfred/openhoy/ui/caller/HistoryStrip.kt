package uk.hairyfred.openhoy.ui.caller

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uk.hairyfred.openhoy.model.Card

@Composable
fun HistoryStrip(
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
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
        )
        val state = rememberLazyListState()
        // Snap back to the front whenever a new call lands so the latest card
        // is always visible without the caller having to scroll.
        LaunchedEffect(history.firstOrNull()) {
            if (history.isNotEmpty()) state.scrollToItem(0)
        }
        LazyRow(
            state = state,
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Most recent first, including the card currently shown big.
            items(history, key = { it.historyKey }) { card ->
                MiniCardView(
                    card = card,
                    fourColourDeck = fourColourDeck,
                    modifier = Modifier
                        .width(72.dp)
                        .clickable { onCardTap(card) },
                )
            }
        }
    }
}
