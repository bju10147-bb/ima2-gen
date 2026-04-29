package com.ima2gen.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ima2gen.app.data.local.SecureKeyStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val secureKeyStore: SecureKeyStore
) : ViewModel() {

    private val _apiKeyLast4 = MutableStateFlow<String?>(null)
    val apiKeyLast4: StateFlow<String?> = _apiKeyLast4.asStateFlow()

    init {
        loadApiKeyInfo()
    }

    private fun loadApiKeyInfo() {
        val key = secureKeyStore.getApiKey()
        if (key != null && key.length >= 4) {
            _apiKeyLast4.value = "sk-...${key.takeLast(4)}"
        } else {
            _apiKeyLast4.value = null
        }
    }

    fun clearApiKey(onCleared: () -> Unit) {
        viewModelScope.launch {
            secureKeyStore.clearApiKey()
            _apiKeyLast4.value = null
            onCleared()
        }
    }
}
