package uk.hairyfred.openhoy.ui.caller

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.hairyfred.openhoy.model.Card
import uk.hairyfred.openhoy.model.DeckState
import uk.hairyfred.openhoy.settings.Settings
import uk.hairyfred.openhoy.viewmodel.CallerViewModel

// A real playing card is taller than it is wide; keep that shape in landscape.
private const val CARD_ASPECT = 0.72f

@Composable
fun CallerScreen(
    vm: CallerViewModel,
    onOpenSettings: () -> Unit,
) {
    val deck by vm.deck.collectAsState()
    val settings by vm.settings.collectAsState()
    val paused by vm.paused.collectAsState()
    val isLandscape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        topBar = {
            OpenHoyTopBar(
                remaining = deck.remaining,
                total = deck.total,
                compact = isLandscape,
                showPauseToggle = settings.autoAdvanceEnabled,
                paused = paused,
                onTogglePaused = vm::togglePaused,
                onOpenSettings = onOpenSettings,
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            if (maxWidth > maxHeight) {
                LandscapeLayout(deck, settings, onNext = { vm.next() }, onReplay = vm::replaySpoken)
            } else {
                PortraitLayout(deck, settings, onNext = { vm.next() }, onReplay = vm::replaySpoken)
            }
        }
    }
}

/**
 * Header bar. In landscape it shrinks (`compact`) to free up the scarce vertical
 * space; in portrait it keeps a normal app-bar height.
 */
@Composable
private fun OpenHoyTopBar(
    remaining: Int,
    total: Int,
    compact: Boolean,
    showPauseToggle: Boolean,
    paused: Boolean,
    onTogglePaused: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val barHeight: Dp = if (compact) 38.dp else 56.dp
    val titleSize = if (compact) 17.sp else 22.sp
    val counterSize = if (compact) 14.sp else 17.sp
    val buttonSize: Dp = if (compact) 38.dp else 48.dp
    val iconSize: Dp = if (compact) 20.dp else 24.dp

    Surface(color = MaterialTheme.colorScheme.surface) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(barHeight)
                .padding(start = 16.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "OpenHoy",
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Black,
                fontSize = titleSize,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "$remaining/$total",
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = counterSize,
                modifier = Modifier.padding(end = 4.dp),
            )
            if (showPauseToggle) {
                IconButton(onClick = onTogglePaused, modifier = Modifier.size(buttonSize)) {
                    Icon(
                        imageVector = if (paused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                        contentDescription = if (paused) "Resume auto-advance" else "Pause auto-advance",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(iconSize),
                    )
                }
            }
            IconButton(onClick = onOpenSettings, modifier = Modifier.size(buttonSize)) {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(iconSize),
                )
            }
        }
    }
}

@Composable
private fun PortraitLayout(
    deck: DeckState,
    settings: Settings,
    onNext: () -> Unit,
    onReplay: (Card) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            CardArea(deck, settings, Modifier.fillMaxSize())
        }

        ActionButton(
            deck = deck,
            onNext = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 88.dp),
        )

        HistoryStrip(
            history = deck.history,
            fourColourDeck = settings.fourColourDeck,
            onCardTap = onReplay,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun LandscapeLayout(
    deck: DeckState,
    settings: Settings,
    onNext: () -> Unit,
    onReplay: (Card) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Left: the card, kept at real-card proportions.
        Box(
            modifier = Modifier
                .weight(1.3f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center,
        ) {
            CardArea(
                deck = deck,
                settings = settings,
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(CARD_ASPECT, matchHeightConstraintsFirst = true),
            )
        }

        // Right: a normal-size NEXT CARD button above the animated history grid.
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ActionButton(
                deck = deck,
                onNext = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 88.dp),
            )
            HistoryGrid(
                history = deck.history,
                fourColourDeck = settings.fourColourDeck,
                onCardTap = onReplay,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
        }
    }
}

@Composable
private fun CardArea(
    deck: DeckState,
    settings: Settings,
    modifier: Modifier = Modifier,
) {
    val current = deck.current
    if (current != null) {
        BigCardView(
            card = current,
            fourColourDeck = settings.fourColourDeck,
            showSuitName = settings.showSuitName,
            modifier = modifier,
        )
    } else {
        PlaceholderCard(deckFinished = deck.finished, modifier = modifier)
    }
}

@Composable
private fun ActionButton(
    deck: DeckState,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val notStarted = deck.history.isEmpty()
    Button(
        onClick = onNext,
        enabled = !deck.finished,
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        contentPadding = PaddingValues(vertical = 20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
    ) {
        Text(
            text = when {
                deck.finished -> "DECK FINISHED"
                notStarted -> "START GAME"
                else -> "NEXT CARD"
            },
            fontWeight = FontWeight.Black,
            fontSize = 32.sp,
        )
    }
}

@Composable
private fun PlaceholderCard(deckFinished: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (deckFinished) "Reshuffle to play again" else "Tap START GAME to begin",
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
        )
    }
}
