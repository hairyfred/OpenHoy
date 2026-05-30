package uk.hairyfred.openhoy.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import uk.hairyfred.openhoy.model.JokerMode
import uk.hairyfred.openhoy.settings.Settings
import uk.hairyfred.openhoy.viewmodel.CallerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    vm: CallerViewModel,
    onBack: () -> Unit,
) {
    val s by vm.settings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            SettingSwitch(
                title = "Speak each card aloud",
                summary = "Reads out the rank and suit when a card is drawn",
                checked = s.ttsEnabled,
                onCheckedChange = { vm.setTts(it) },
            )

            SettingSwitch(
                title = "Auto-advance",
                summary = "Automatically draw the next card after a delay",
                checked = s.autoAdvanceEnabled,
                onCheckedChange = { vm.setAutoAdvance(it) },
            )

            AutoAdvanceSlider(
                enabled = s.autoAdvanceEnabled,
                seconds = s.autoAdvanceSeconds,
                onSecondsChange = { vm.setAutoAdvanceSeconds(it) },
            )

            SettingSwitch(
                title = "Use 4-colour deck",
                summary = "Clubs green, spades black, hearts red, diamonds blue",
                checked = s.fourColourDeck,
                onCheckedChange = { vm.setFourColour(it) },
            )

            SettingSwitch(
                title = "Show suit name on card",
                summary = "Writes CLUBS / SPADES / HEARTS / DIAMONDS in large letters",
                checked = s.showSuitName,
                onCheckedChange = { vm.setShowSuitName(it) },
            )

            JokerModePicker(
                selected = s.jokerMode,
                onSelect = { vm.setJokerMode(it) },
            )
        }
    }
}

@Composable
private fun JokerModePicker(
    selected: JokerMode,
    onSelect: (JokerMode) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Jokers",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = "Takes effect on the next shuffle.",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        JokerOption(
            label = "Off",
            summary = "Standard 52-card deck",
            selected = selected == JokerMode.NONE,
            onSelect = { onSelect(JokerMode.NONE) },
        )
        JokerOption(
            label = "1 joker",
            summary = "53 cards — one plain joker",
            selected = selected == JokerMode.ONE,
            onSelect = { onSelect(JokerMode.ONE) },
        )
        JokerOption(
            label = "2 jokers (red and black)",
            summary = "54 cards — one red joker and one black joker",
            selected = selected == JokerMode.TWO_COLOURS,
            onSelect = { onSelect(JokerMode.TWO_COLOURS) },
        )
    }
}

@Composable
private fun JokerOption(
    label: String,
    summary: String,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onSelect,
                role = Role.RadioButton,
            )
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = Color(0xFF8BC34A),
                unselectedColor = Color(0xFFCFD8DC),
            ),
        )
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = summary,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
private fun SettingSwitch(
    title: String,
    summary: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier
            .weight(1f, fill = true)
            .padding(end = 16.dp)) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = summary,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = highContrastSwitchColors(),
        )
    }
}

@Composable
private fun AutoAdvanceSlider(
    enabled: Boolean,
    seconds: Int,
    onSecondsChange: (Int) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Delay between cards: $seconds seconds",
            color = MaterialTheme.colorScheme.onBackground.copy(
                alpha = if (enabled) 1f else 0.5f,
            ),
            style = MaterialTheme.typography.titleLarge,
        )
        Slider(
            value = seconds.toFloat(),
            onValueChange = { onSecondsChange(it.toInt()) },
            valueRange = Settings.MIN_SECONDS.toFloat()..Settings.MAX_SECONDS.toFloat(),
            steps = Settings.MAX_SECONDS - Settings.MIN_SECONDS - 1,
            enabled = enabled,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color(0xFF8BC34A),
                inactiveTrackColor = Color(0xFF263238),
                activeTickColor = Color.White,
                inactiveTickColor = Color(0xFF607D8B),
            ),
        )
    }
}

/**
 * Default Material3 Switch colours render the checked track in [primary], which
 * matches our dark green background and makes the thumb appear to float alone.
 * Force high-contrast colours so the on/off state is obvious against the felt.
 */
@Composable
private fun highContrastSwitchColors() = SwitchDefaults.colors(
    checkedThumbColor = Color.White,
    checkedTrackColor = Color(0xFF8BC34A),       // bright lime
    checkedBorderColor = Color(0xFF8BC34A),
    uncheckedThumbColor = Color(0xFFCFD8DC),
    uncheckedTrackColor = Color(0xFF263238),     // dark blue-grey, clearly not the background
    uncheckedBorderColor = Color(0xFF455A64),
)
