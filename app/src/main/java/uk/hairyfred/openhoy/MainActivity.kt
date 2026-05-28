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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import uk.hairyfred.openhoy.speech.CardSpeaker
import uk.hairyfred.openhoy.ui.caller.CallerScreen
import uk.hairyfred.openhoy.ui.settings.SettingsScreen
import uk.hairyfred.openhoy.ui.theme.OpenHoyTheme
import uk.hairyfred.openhoy.viewmodel.CallerViewModel

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
                    val vm: CallerViewModel = viewModel(
                        factory = CallerViewModel.Factory(application) { speaker },
                    )
                    var showSettings by remember { mutableStateOf(false) }

                    // System back from Settings returns to the caller screen
                    // instead of finishing the activity.
                    BackHandler(enabled = showSettings) { showSettings = false }

                    AnimatedContent(
                        targetState = showSettings,
                        label = "screen",
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                    ) { settingsOpen ->
                        if (settingsOpen) {
                            SettingsScreen(vm = vm, onBack = { showSettings = false })
                        } else {
                            CallerScreen(vm = vm, onOpenSettings = { showSettings = true })
                        }
                    }
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
