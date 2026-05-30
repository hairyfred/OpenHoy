package uk.hairyfred.openhoy.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import uk.hairyfred.openhoy.model.HoySheet
import uk.hairyfred.openhoy.model.SuitedCard
import uk.hairyfred.openhoy.settings.Settings
import uk.hairyfred.openhoy.settings.SettingsRepository

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = SettingsRepository(application)

    val settings: StateFlow<Settings> = repo.settings.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        Settings(),
    )

    private val _sheet = MutableStateFlow(HoySheet.random())
    val sheet: StateFlow<HoySheet> = _sheet.asStateFlow()

    fun toggle(card: SuitedCard) {
        _sheet.value = _sheet.value.toggle(card)
    }

    fun newSheet() {
        _sheet.value = HoySheet.random()
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            PlayerViewModel(application) as T
    }
}
