package uk.hairyfred.openhoy.ui.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.hairyfred.openhoy.model.HoySheet
import uk.hairyfred.openhoy.model.SuitedCard
import uk.hairyfred.openhoy.ui.caller.SuitIcon
import uk.hairyfred.openhoy.ui.theme.CardWhite
import uk.hairyfred.openhoy.viewmodel.PlayerViewModel

private val MarkerGreen = Color(0xFF2E7D32)
private val HoyBannerColor = Color(0xFFFFB300)

@Composable
fun PlayerScreen(
    vm: PlayerViewModel,
    onOpenSettings: () -> Unit,
) {
    val sheet by vm.sheet.collectAsState()
    val settings by vm.settings.collectAsState()

    Scaffold(
        topBar = {
            PlayerTopBar(
                marked = sheet.markedCount,
                total = sheet.total,
                onOpenSettings = onOpenSettings,
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            HoyBanner(
                visible = sheet.isComplete,
                onNewSheet = { vm.newSheet() },
            )
            SheetGrid(
                sheet = sheet,
                fourColourDeck = settings.fourColourDeck,
                onTap = { vm.toggle(it) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun PlayerTopBar(
    marked: Int,
    total: Int,
    onOpenSettings: () -> Unit,
) {
    val barHeight: Dp = 56.dp
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
                fontSize = 22.sp,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "$marked/$total",
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                modifier = Modifier.padding(end = 4.dp),
            )
            IconButton(onClick = onOpenSettings) {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun HoyBanner(visible: Boolean, onNewSheet: () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(initialScale = 0.92f),
        exit = fadeOut() + scaleOut(targetScale = 0.92f),
    ) {
        Surface(
            color = HoyBannerColor,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "HOY!",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 56.sp,
                    )
                    Text(
                        text = "All cards marked",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .clickable(onClick = onNewSheet)
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                ) {
                    Text(
                        text = "NEW SHEET",
                        color = HoyBannerColor,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun SheetGrid(
    sheet: HoySheet,
    fourColourDeck: Boolean,
    onTap: (SuitedCard) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(4.dp),
        modifier = modifier,
    ) {
        items(sheet.cards, key = { it.historyKey }) { card ->
            SheetCard(
                card = card,
                marked = card in sheet.marked,
                fourColourDeck = fourColourDeck,
                onTap = { onTap(card) },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.74f),
            )
        }
    }
}

@Composable
private fun SheetCard(
    card: SuitedCard,
    marked: Boolean,
    fourColourDeck: Boolean,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tint = card.suit.colour(fourColourDeck)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CardWhite)
            .border(3.dp, tint, RoundedCornerShape(16.dp))
            .clickable(onClick = onTap),
        contentAlignment = Alignment.Center,
    ) {
        // Card face — dimmed when marked so the chip stands out.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .alpha(if (marked) 0.35f else 1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = card.rank.symbol,
                color = tint,
                fontWeight = FontWeight.Black,
                fontSize = 56.sp,
            )
            SuitIcon(
                suit = card.suit,
                tint = tint,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(52.dp),
            )
            Text(
                text = card.suit.displayName,
                color = tint,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 4.dp),
            )
        }

        // Counter chip — mimics the physical token a player would place on a
        // matched cell. Always centred regardless of card content size.
        if (marked) {
            MarkerChip()
        }
    }
}

@Composable
private fun MarkerChip() {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(MarkerGreen)
            .border(3.dp, Color.White, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = "marked",
            tint = Color.White,
            modifier = Modifier.size(48.dp),
        )
    }
}
