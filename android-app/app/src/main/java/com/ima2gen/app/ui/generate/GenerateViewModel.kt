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

    // ── Session & Presets ──
    val sessions: StateFlow<List<SessionEntity>> = historyDao.getSessionsForProject(projectId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val presets = historyDao.getAllPresets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedSessionId = MutableStateFlow<String?>(null)
    val selectedSessionId: StateFlow<String?> = _selectedSessionId.asStateFlow()

    private val _selectedPresetId = MutableStateFlow<String?>(null)
    val selectedPresetId: StateFlow<String?> = _selectedPresetId.asStateFlow()

    // ── Session Data Management ──
    private val promptDrafts = mutableMapOf<String, String>()
    
    // Observed history for the selected session
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val sessionHistory: StateFlow<List<HistoryEntity>> = _selectedSessionId.flatMapLatest { id ->
        if (id != null) historyDao.getHistoryForSession(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _prompt = MutableStateFlow("")
    val prompt: StateFlow<String> = _prompt.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    // The image(s) currently being viewed in the main area
    private val _displayImages = MutableStateFlow<List<GeneratedImage>>(emptyList())
    val displayImages: StateFlow<List<GeneratedImage>> = _displayImages.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _elapsedTime = MutableStateFlow(0)
    val elapsedTime: StateFlow<Int> = _elapsedTime.asStateFlow()

    // ── Options ──
    private val _selectedModel = MutableStateFlow("dall-e-3")
    val selectedModel = _selectedModel.asStateFlow()
    private val _selectedSize = MutableStateFlow("1024x1024")
    val selectedSize = _selectedSize.asStateFlow()
    private val _selectedQuality = MutableStateFlow("standard")
    val selectedQuality = _selectedQuality.asStateFlow()
    private val _selectedCount = MutableStateFlow(1)
    val selectedCount = _selectedCount.asStateFlow()
    private val _selectedFormat = MutableStateFlow("png")
    val selectedFormat = _selectedFormat.asStateFlow()
    private val _selectedModeration = MutableStateFlow("auto")
    val selectedModeration = _selectedModeration.asStateFlow()

    private val _estimatedCost = MutableStateFlow(0.0)
    val estimatedCost = _estimatedCost.asStateFlow()

    private val _referenceImages = MutableStateFlow<List<android.net.Uri>>(emptyList())
    val referenceImages = _referenceImages.asStateFlow()

    init {
        viewModelScope.launch {
            sessions.collect { list ->
                if (_selectedSessionId.value == null && list.isNotEmpty()) {
                    selectSession(list.first().id)
                }
            }
        }
        viewModelScope.launch {
            combine(_selectedModel, _selectedSize, _selectedQuality, _selectedCount) { m, s, q, c ->
                calculateCost(m, s, q, c)
            }.collect { _estimatedCost.value = it }
        }
    }

    private fun calculateCost(model: String, size: String, quality: String, count: Int): Double {
        val perImage = if (model == "dall-e-3") {
            val is2K = size.startsWith("2048") || size.endsWith("2048")
            val isWideOrTall = size != "1024x1024" && !is2K
            val isHd = quality == "hd"
            
            when {
                is2K -> 0.160 // High resolution premium
                isWideOrTall && isHd -> 0.120
                isWideOrTall || isHd -> 0.080
                else -> 0.040
            }
        } else { // dall-e-2
            when (size) {
                "1024x1024" -> 0.020
                "512x512" -> 0.018
                "256x256" -> 0.016
                else -> 0.020
            }
        }
        return perImage * count
    }

    fun onPromptChanged(newPrompt: String) { 
        _prompt.value = newPrompt
        _selectedSessionId.value?.let { promptDrafts[it] = newPrompt }
    }

    fun onModelChanged(model: String) { 
        _selectedModel.value = model 
        if (model == "dall-e-2") {
            _selectedQuality.value = "standard"
            if (!_selectedSize.value.contains("x") || (_selectedSize.value != "1024x1024" && _selectedSize.value != "512x512" && _selectedSize.value != "256x256")) _selectedSize.value = "1024x1024"
        } else if (model == "dall-e-3") {
            if (_selectedSize.value == "512x512" || _selectedSize.value == "256x256") _selectedSize.value = "1024x1024"
        }
    }
    fun onSizeChanged(size: String) { _selectedSize.value = size }
    fun onQualityChanged(quality: String) { _selectedQuality.value = quality }
    fun onCountChanged(count: Int) { _selectedCount.value = count }
    fun onFormatChanged(f: String) { _selectedFormat.value = f }
    fun onModerationChanged(m: String) { _selectedModeration.value = m }

    fun selectSession(sessionId: String) {
        _selectedSessionId.value?.let { oldId -> promptDrafts[oldId] = _prompt.value }
        _selectedSessionId.value = sessionId
        _prompt.value = promptDrafts[sessionId] ?: ""
        _displayImages.value = emptyList() // Reset main view when switching
        _selectedPresetId.value = null
    }

    fun selectHistoryItem(item: HistoryEntity) {
        _displayImages.value = listOf(GeneratedImage(image = item.imageUrl, revisedPrompt = item.revisedPrompt))
    }

    fun generateImage() {
        val sessionId = _selectedSessionId.value ?: return
        if (_prompt.value.isBlank()) return
        
        viewModelScope.launch {
            _isGenerating.value = true
            _errorMessage.value = null
            _elapsedTime.value = 0
            
            val timerJob = launch { while (true) { delay(1000); _elapsedTime.value += 1 } }

            try {
                delay(2000) // Mock
                val count = _selectedCount.value
                val mockImages = List(count) { i ->
                    GeneratedImage(
                        image = "https://picsum.photos/seed/${UUID.randomUUID()}/1024/1024",
                        revisedPrompt = "AI optimization for: ${_prompt.value} (#${i+1})"
                    )
                }

                _displayImages.value = mockImages
                
                mockImages.forEach { genImage ->
                    historyDao.insertHistory(
                        HistoryEntity(
                            sessionId = sessionId,
                            prompt = _prompt.value,
                            revisedPrompt = genImage.revisedPrompt,
                            imageUrl = genImage.image
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

    fun createSession(name: String) {
        viewModelScope.launch {
            val newSession = SessionEntity(projectId = projectId, name = name)
            historyDao.insertSession(newSession)
            selectSession(newSession.id)
        }
    }
    fun deleteSession(id: String) {
        viewModelScope.launch {
            historyDao.deleteSession(id)
            promptDrafts.remove(id)
            if (_selectedSessionId.value == id) _selectedSessionId.value = null
        }
    }
    fun selectPreset(p: com.ima2gen.app.data.local.db.PromptPresetEntity?) {
        _selectedPresetId.value = p?.id
        p?.let { _prompt.value = it.content }
    }
    fun createPreset(n: String, c: String) {
        viewModelScope.launch {
            val newPreset = com.ima2gen.app.data.local.db.PromptPresetEntity(name = n, content = c)
            historyDao.insertPreset(newPreset)
            _selectedPresetId.value = newPreset.id
        }
    }
    fun deletePreset(id: String) {
        viewModelScope.launch { historyDao.deletePreset(id); if (_selectedPresetId.value == id) _selectedPresetId.value = null }
    }
    fun addReferenceImages(uris: List<android.net.Uri>) { /*...*/ }
    fun removeReferenceImage(uri: android.net.Uri) { /*...*/ }
    fun cancelGeneration() { _isGenerating.value = false }
    fun dismissError() { _errorMessage.value = null }
}
