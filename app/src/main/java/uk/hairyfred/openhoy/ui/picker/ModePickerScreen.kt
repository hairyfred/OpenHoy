package uk.hairyfred.openhoy.ui.picker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Launch screen: ask which side of the table the user is on. They can still
 * switch modes mid-session via the top-bar icon — this screen just removes the
 * implicit assumption that everyone is the caller.
 */
@Composable
fun ModePickerScreen(
    onChooseCaller: () -> Unit,
    onChoosePlayer: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 32.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "OpenHoy",
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Black,
                fontSize = 52.sp,
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "How would you like to use it?",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.size(40.dp))
            ModeButton(
                label = "I'm the Caller",
                subtitle = "Show and announce cards to the players",
                icon = Icons.Filled.Campaign,
                onClick = onChooseCaller,
            )
            Spacer(modifier = Modifier.size(24.dp))
            ModeButton(
                label = "I'm a Player",
                subtitle = "Get a Hoy sheet to mark off as cards are called",
                icon = Icons.Filled.GridView,
                onClick = onChoosePlayer,
            )
        }
    }
}

@Composable
private fun ModeButton(
    label: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 132.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(64.dp),
        )
        Spacer(modifier = Modifier.width(20.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = label,
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 28.sp,
            )
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.88f),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
        }
    }
}
