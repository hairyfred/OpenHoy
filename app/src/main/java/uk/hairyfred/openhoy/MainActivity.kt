package uk.hairyfred.openhoy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import uk.hairyfred.openhoy.speech.CardSpeaker
import uk.hairyfred.openhoy.ui.caller.CallerScreen
import uk.hairyfred.openhoy.ui.picker.ModePickerScreen
import uk.hairyfred.openhoy.ui.player.PlayerScreen
import uk.hairyfred.openhoy.ui.settings.SettingsScreen
import uk.hairyfred.openhoy.ui.theme.OpenHoyTheme
import uk.hairyfred.openhoy.viewmodel.CallerViewModel
import uk.hairyfred.openhoy.viewmodel.PlayerViewModel

private enum class Screen { Picker, Caller, Player, Settings }

class MainActivity : ComponentActivity() {

    private var speaker: CardSpeaker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        speaker = CardSpeaker(applicationContext)

        setContent {
            OpenHoyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val callerVm: CallerViewModel = viewModel(
                        factory = CallerViewModel.Factory(application) { speaker },
                    )
                    val playerVm: PlayerViewModel = viewModel(
                        factory = PlayerViewModel.Factory(application),
                    )
                    AppNavigation(callerVm = callerVm, playerVm = playerVm)
                }
            }
        }
    }

    override fun onDestroy() {
        speaker?.shutdown()
        speaker = null
        super.onDestroy()
    }
}

/**
 * Simple back-stack navigation:
 *   * Initial: [Picker].
 *   * Choosing a mode pushes Caller / Player onto the stack.
 *   * Switching modes via the in-screen icon *replaces* the top, so back from
 *     either mode still lands on Picker.
 *   * Settings pushes (back returns to whichever mode opened it).
 *   * Android back pops the stack; at Picker it falls through and exits.
 */
@Composable
private fun AppNavigation(
    callerVm: CallerViewModel,
    playerVm: PlayerViewModel,
) {
    val stack = remember { mutableStateListOf(Screen.Picker) }
    val current = stack.last()
    var showQuitDialog by remember { mutableStateOf(false) }

    fun pop() {
        if (stack.size > 1) stack.removeAt(stack.lastIndex)
    }

    fun push(target: Screen) {
        stack.add(target)
    }

    fun handleBack() {
        // Going back to the picker ends the round — confirm first. Settings
        // back (or anything deeper) just pops without prompting.
        if (stack.size == 2) showQuitDialog = true else pop()
    }

    if (current != Screen.Picker) {
        BackHandler { handleBack() }
    }

    if (showQuitDialog) {
        AlertDialog(
            onDismissRequest = { showQuitDialog = false },
            title = { Text("Quit?") },
            text = { Text("Are you sure you want to quit?") },
            confirmButton = {
                TextButton(onClick = {
                    showQuitDialog = false
                    // Reset both modes so re-entering starts a fresh round.
                    callerVm.reshuffle()
                    playerVm.newSheet()
                    pop()
                }) { Text("Quit") }
            },
            dismissButton = {
                TextButton(onClick = { showQuitDialog = false }) { Text("Stay") }
            },
        )
    }

    AnimatedContent(
        targetState = current,
        label = "screen",
        transitionSpec = { fadeIn() togetherWith fadeOut() },
    ) { screen ->
        when (screen) {
            Screen.Picker -> ModePickerScreen(
                onChooseCaller = { push(Screen.Caller) },
                onChoosePlayer = { push(Screen.Player) },
            )

            Screen.Caller -> CallerScreen(
                vm = callerVm,
                onOpenSettings = { push(Screen.Settings) },
            )

            Screen.Player -> PlayerScreen(
                vm = playerVm,
                onOpenSettings = { push(Screen.Settings) },
            )

            Screen.Settings -> SettingsScreen(
                vm = callerVm,
                onBack = { pop() },
            )
        }
    }
}
