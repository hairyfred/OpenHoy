package uk.hairyfred.openhoy.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "openhoy_settings")

class SettingsRepository(private val context: Context) {

    val settings: Flow<Settings> = context.dataStore.data.map { prefs ->
        Settings(
            ttsEnabled = prefs[K_TTS] ?: true,
            autoAdvanceEnabled = prefs[K_AUTO] ?: false,
            autoAdvanceSeconds = (prefs[K_AUTO_SECONDS] ?: 5)
                .coerceIn(Settings.MIN_SECONDS, Settings.MAX_SECONDS),
            fourColourDeck = prefs[K_FOUR_COLOUR] ?: true,
            showSuitName = prefs[K_SUIT_NAME] ?: true,
        )
    }

    suspend fun setTtsEnabled(value: Boolean) =
        context.dataStore.edit { it[K_TTS] = value }.let { }

    suspend fun setAutoAdvanceEnabled(value: Boolean) =
        context.dataStore.edit { it[K_AUTO] = value }.let { }

    suspend fun setAutoAdvanceSeconds(value: Int) =
        context.dataStore.edit {
            it[K_AUTO_SECONDS] = value.coerceIn(Settings.MIN_SECONDS, Settings.MAX_SECONDS)
        }.let { }

    suspend fun setFourColourDeck(value: Boolean) =
        context.dataStore.edit { it[K_FOUR_COLOUR] = value }.let { }

    suspend fun setShowSuitName(value: Boolean) =
        context.dataStore.edit { it[K_SUIT_NAME] = value }.let { }

    private companion object {
        val K_TTS = booleanPreferencesKey("tts_enabled")
        val K_AUTO = booleanPreferencesKey("auto_advance_enabled")
        val K_AUTO_SECONDS = intPreferencesKey("auto_advance_seconds")
        val K_FOUR_COLOUR = booleanPreferencesKey("four_colour_deck")
        val K_SUIT_NAME = booleanPreferencesKey("show_suit_name")
    }
}
