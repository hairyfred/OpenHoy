package uk.hairyfred.openhoy.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uk.hairyfred.openhoy.model.Card
import uk.hairyfred.openhoy.model.DeckState
import uk.hairyfred.openhoy.model.JokerMode
import uk.hairyfred.openhoy.settings.Settings
import uk.hairyfred.openhoy.settings.SettingsRepository
import uk.hairyfred.openhoy.speech.CardSpeaker

class CallerViewModel(
    application: Application,
    private val speakerProvider: () -> CardSpeaker?,
) : AndroidViewModel(application) {

    private val repo = SettingsRepository(application)

    val settings: StateFlow<Settings> = repo.settings.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        Settings(),
    )

    private val _deck = MutableStateFlow(DeckState.freshShuffled(JokerMode.NONE))
    val deck: StateFlow<DeckState> = _deck.asStateFlow()

    private var autoJob: Job? = null

    init {
        // The initial deck is built with default settings (no jokers). Once
        // persisted settings have loaded, rebuild it once if the game hasn't
        // started yet — so a returning user gets a deck matching their saved
        // joker preference without needing to reshuffle.
        viewModelScope.launch {
            val s = repo.settings.first()
            if (_deck.value.drawnCount == 0 && s.jokerMode != JokerMode.NONE) {
                _deck.value = DeckState.freshShuffled(s.jokerMode)
            }
        }
    }

    fun next() {
        val before = _deck.value
        if (before.finished) return
        val after = before.drawNext()
        _deck.value = after
        after.current?.let { speakIfEnabled(it) }
        scheduleAutoAdvanceIfEnabled()
    }

    fun reshuffle() {
        autoJob?.cancel()
        autoJob = null
        _deck.value = DeckState.freshShuffled(settings.value.jokerMode)
    }

    fun replaySpoken(card: Card) {
        speakIfEnabled(card)
    }

    private fun speakIfEnabled(card: Card) {
        if (settings.value.ttsEnabled) speakerProvider()?.speak(card)
    }

    private fun scheduleAutoAdvanceIfEnabled() {
        autoJob?.cancel()
        val s = settings.value
        if (!s.autoAdvanceEnabled || _deck.value.finished) return
        autoJob = viewModelScope.launch {
            delay(s.autoAdvanceSeconds * 1000L)
            next()
        }
    }

    fun setTts(value: Boolean) = viewModelScope.launch { repo.setTtsEnabled(value) }
    fun setAutoAdvance(value: Boolean) = viewModelScope.launch {
        repo.setAutoAdvanceEnabled(value)
        if (!value) {
            autoJob?.cancel()
            autoJob = null
        }
        // If switched ON, the timer starts on the next manual draw — gives the
        // caller a chance to position the device before cards start auto-flipping.
    }
    fun setAutoAdvanceSeconds(value: Int) = viewModelScope.launch {
        repo.setAutoAdvanceSeconds(value)
    }
    fun setFourColour(value: Boolean) = viewModelScope.launch { repo.setFourColourDeck(value) }
    fun setShowSuitName(value: Boolean) = viewModelScope.launch { repo.setShowSuitName(value) }
    fun setJokerMode(value: JokerMode) = viewModelScope.launch {
        repo.setJokerMode(value)
        // Apply immediately if the game hasn't started; otherwise wait for the
        // next reshuffle so we don't yank cards from a round in progress.
        if (_deck.value.drawnCount == 0) {
            _deck.value = DeckState.freshShuffled(value)
        }
    }

    class Factory(
        private val application: Application,
        private val speakerProvider: () -> CardSpeaker?,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            CallerViewModel(application, speakerProvider) as T
    }
}
