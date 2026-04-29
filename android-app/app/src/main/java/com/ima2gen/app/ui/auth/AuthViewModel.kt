package com.ima2gen.app.ui.auth

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
class AuthViewModel @Inject constructor(
    private val secureKeyStore: SecureKeyStore,
) : ViewModel() {

    private val _apiKey = MutableStateFlow(secureKeyStore.getApiKey() ?: "")
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()

    private val _serverUrl = MutableStateFlow(secureKeyStore.getServerUrl())
    val serverUrl: StateFlow<String> = _serverUrl.asStateFlow()

    private val _isKeyValidFormat = MutableStateFlow(false)
    val isKeyValidFormat: StateFlow<Boolean> = _isKeyValidFormat.asStateFlow()

    init {
        validateKeyFormat(_apiKey.value)
    }

    fun onApiKeyChanged(key: String) {
        _apiKey.value = key
        validateKeyFormat(key)
    }

    private fun validateKeyFormat(key: String) {
        _isKeyValidFormat.value = secureKeyStore.isValidKeyFormat(key)
    }

    fun onServerUrlChanged(url: String) {
        _serverUrl.value = url
    }

    fun saveConfiguration(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (_isKeyValidFormat.value) {
                secureKeyStore.saveApiKey(_apiKey.value)
                secureKeyStore.saveServerUrl(_serverUrl.value)
                onSuccess()
            }
        }
    }
    
    fun saveServerUrlOnly(onSuccess: () -> Unit) {
        viewModelScope.launch {
             secureKeyStore.saveServerUrl(_serverUrl.value)
             onSuccess()
        }
    }
}
