package com.ima2gen.app.ui.generate

import android.util.Base64
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ima2gen.app.data.api.OpenAiApi
import com.ima2gen.app.data.api.OpenAiImageRequest
import com.ima2gen.app.data.local.db.HistoryDao
import com.ima2gen.app.data.local.db.HistoryEntity
import com.ima2gen.app.data.local.db.SessionEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class GeneratedImage(
    val image: String,
    val revisedPrompt: String? = null
)

@HiltViewModel
class GenerateViewModel @Inject constructor(
    private val historyDao: HistoryDao,
    private val openAiApi: OpenAiApi,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val projectId: String = checkNotNull(savedStateHandle["projectId"])

    // ── Session Management ──
    val sessions: StateFlow<List<SessionEntity>> = historyDao.getSessionsForProject(projectId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedSessionId = MutableStateFlow<String?>(null)
    val selectedSessionId: StateFlow<String?> = _selectedSessionId.asStateFlow()

    private val _prompt = MutableStateFlow("")
    val prompt: StateFlow<String> = _prompt.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _generatedImages = MutableStateFlow<List<GeneratedImage>>(emptyList())
    val generatedImages: StateFlow<List<GeneratedImage>> = _generatedImages.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _elapsedTime = MutableStateFlow(0)
    val elapsedTime: StateFlow<Int> = _elapsedTime.asStateFlow()

    // ── Generation Options ──
    private val _selectedModel = MutableStateFlow("dall-e-3")
    val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()

    private val _selectedSize = MutableStateFlow("1024x1024")
    val selectedSize: StateFlow<String> = _selectedSize.asStateFlow()

    private val _selectedQuality = MutableStateFlow("standard")
    val selectedQuality: StateFlow<String> = _selectedQuality.asStateFlow()

    private val _selectedCount = MutableStateFlow(1)
    val selectedCount: StateFlow<Int> = _selectedCount.asStateFlow()

    private val _referenceImages = MutableStateFlow<List<android.net.Uri>>(emptyList())
    val referenceImages: StateFlow<List<android.net.Uri>> = _referenceImages.asStateFlow()

    init {
        // Auto-select first session if available
        viewModelScope.launch {
            sessions.collect { list ->
                if (_selectedSessionId.value == null && list.isNotEmpty()) {
                    _selectedSessionId.value = list.first().id
                }
            }
        }
    }

    fun onPromptChanged(newPrompt: String) { _prompt.value = newPrompt }
    fun onModelChanged(model: String) { 
        _selectedModel.value = model 
        if (model == "dall-e-2") {
            _selectedQuality.value = "standard"
            if (!_selectedSize.value.contains("x") || (_selectedSize.value != "1024x1024" && _selectedSize.value != "512x512" && _selectedSize.value != "256x256")) {
                _selectedSize.value = "1024x1024"
            }
        } else if (model == "dall-e-3") {
            if (_selectedSize.value == "512x512" || _selectedSize.value == "256x256") {
                _selectedSize.value = "1024x1024"
            }
        }
    }
    fun onSizeChanged(size: String) { _selectedSize.value = size }
    fun onQualityChanged(quality: String) { _selectedQuality.value = quality }
    fun onCountChanged(count: Int) { _selectedCount.value = count }

    fun selectSession(sessionId: String) {
        _selectedSessionId.value = sessionId
    }

    fun createSession(name: String) {
        viewModelScope.launch {
            val newSession = SessionEntity(projectId = projectId, name = name)
            historyDao.insertSession(newSession)
            _selectedSessionId.value = newSession.id
        }
    }

    fun deleteSession(id: String) {
        viewModelScope.launch {
            historyDao.deleteSession(id)
            if (_selectedSessionId.value == id) {
                _selectedSessionId.value = null
            }
        }
    }

    fun addReferenceImages(uris: List<android.net.Uri>) {
        val current = _referenceImages.value.toMutableList()
        current.addAll(uris)
        _referenceImages.value = current.take(5)
    }

    fun removeReferenceImage(uri: android.net.Uri) {
        _referenceImages.value = _referenceImages.value.filter { it != uri }
    }

    fun generateImage() {
        val sessionId = _selectedSessionId.value
        if (_prompt.value.isBlank() || sessionId == null) {
            if (sessionId == null) _errorMessage.value = "세션을 먼저 선택하거나 생성해주세요."
            return
        }
        
        viewModelScope.launch {
            _isGenerating.value = true
            _errorMessage.value = null
            _generatedImages.value = emptyList()
            _elapsedTime.value = 0
            
            val timerJob = launch {
                while (true) {
                    delay(1000)
                    _elapsedTime.value += 1
                }
            }

            try {
                delay(3000) // Mock delay

                val count = _selectedCount.value
                val size = _selectedSize.value.split("x")
                val width = size.getOrNull(0)?.toIntOrNull() ?: 1024
                val height = size.getOrNull(1)?.toIntOrNull() ?: 1024

                val mockImages = List(count) { index ->
                    val mockImageUrl = "https://picsum.photos/seed/${_prompt.value.hashCode() + index}/$width/$height"
                    val mockRevisedPrompt = "Mock revised prompt (#${index + 1}) for: ${_prompt.value}"
                    GeneratedImage(image = mockImageUrl, revisedPrompt = mockRevisedPrompt)
                }

                _generatedImages.value = mockImages

                // ── Physical Saving to Project Folder ──
                val project = historyDao.getProjectById(projectId)
                val context = com.ima2gen.app.Ima2GenApplication.instance // Assuming we add a global context helper or just use Hilt
                
                mockImages.forEach { genImage ->
                    // Save to history first with mock URL (or local one later)
                    var finalImageUrl = genImage.image
                    
                    // In a real app, we would download the bitmap and save it here
                    // For now, let's just save the history. 
                    // To actually save physically, we need a Context. 
                    // I'll add a way to get context in ViewModel or handle it in a better way.
                    
                    historyDao.insertHistory(
                        HistoryEntity(
                            sessionId = sessionId,
                            prompt = _prompt.value,
                            revisedPrompt = genImage.revisedPrompt,
                            imageUrl = finalImageUrl
                        )
                    )
                }
            } catch (e: Exception) {
                _errorMessage.value = "생성 실패: ${e.localizedMessage}"
            } finally {
                timerJob.cancel()
                _isGenerating.value = false
            }
        }
    }

    fun cancelGeneration() {
        // In real implementation, cancel the API call
        _isGenerating.value = false
    }

    fun dismissError() {
        _errorMessage.value = null
    }
}
