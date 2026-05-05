package com.ima2gen.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ima2gen.app.data.local.SecureKeyStore
import com.ima2gen.app.data.repository.AppLanguage
import com.ima2gen.app.data.repository.AppTheme
import com.ima2gen.app.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val secureKeyStore: SecureKeyStore,
) : ViewModel() {

    val imageModel: StateFlow<String> = settingsRepository.imageModel
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "gpt-5.5")

    val theme: StateFlow<AppTheme> = settingsRepository.theme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppTheme.SYSTEM)

    val language: StateFlow<AppLanguage> = settingsRepository.language
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppLanguage.SYSTEM)

    fun setImageModel(model: String) {
        viewModelScope.launch { settingsRepository.setImageModel(model) }
    }

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch { settingsRepository.setTheme(theme) }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch { settingsRepository.setLanguage(language) }
    }

    fun resetApiKey() {
        secureKeyStore.clearApiKey()
    }
}
