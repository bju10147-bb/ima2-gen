package com.ima2gen.app.ui.generate

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ima2gen.app.data.api.Ima2GenApi
import com.ima2gen.app.data.api.dto.GenerateRequest
import com.ima2gen.app.data.local.db.HistoryDao
import com.ima2gen.app.data.local.db.HistoryEntity
import com.ima2gen.app.data.local.db.PromptPresetEntity
import com.ima2gen.app.data.local.db.SessionEntity
import com.ima2gen.app.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

// Rename to avoid conflict with DTO GeneratedImage
data class UiGeneratedImage(
    val image: String,
    val revisedPrompt: String? = null
)

@HiltViewModel
class GenerateViewModel @Inject constructor(
    private val ima2GenApi: Ima2GenApi,
    private val historyDao: HistoryDao,
    private val settingsRepository: SettingsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val projectId: String = checkNotNull(savedStateHandle["projectId"])

    private val _prompt = MutableStateFlow("")
    val prompt: StateFlow<String> = _prompt

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating

    private val _displayImages = MutableStateFlow<List<UiGeneratedImage>>(emptyList())
    val displayImages: StateFlow<List<UiGeneratedImage>> = _displayImages

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _elapsedTime = MutableStateFlow(0)
    val elapsedTime: StateFlow<Int> = _elapsedTime

    private val _selectedSessionId = MutableStateFlow<String?>(null)
    val selectedSessionId: StateFlow<String?> = _selectedSessionId

    private val _selectedSize = MutableStateFlow("1024x1024")
    val selectedSize: StateFlow<String> = _selectedSize

    private val _selectedQuality = MutableStateFlow("standard")
    val selectedQuality: StateFlow<String> = _selectedQuality

    private val _selectedCount = MutableStateFlow(1)
    val selectedCount: StateFlow<Int> = _selectedCount

    private val _selectedFormat = MutableStateFlow("png")
    val selectedFormat: StateFlow<String> = _selectedFormat

    private val _selectedModeration = MutableStateFlow("low")
    val selectedModeration: StateFlow<String> = _selectedModeration

    private val _selectedPresetId = MutableStateFlow<String?>(null)
    val selectedPresetId: StateFlow<String?> = _selectedPresetId

    val sessions: StateFlow<List<SessionEntity>> = historyDao.getSessionsForProject(projectId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val presets: StateFlow<List<PromptPresetEntity>> = historyDao.getAllPresets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedModel: StateFlow<String> = settingsRepository.imageModel
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "5.4")

    val sessionHistory: StateFlow<List<HistoryEntity>> = _selectedSessionId
        .flatMapLatest { sessionId ->
            if (sessionId != null) historyDao.getHistoryForSession(sessionId)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val estimatedCost: StateFlow<Double> = combine(
        selectedModel, selectedSize, selectedQuality, selectedCount
    ) { model, size, quality, imgCount ->
        calculateCost(model, size, quality, imgCount)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    private var generationJob: Job? = null
    private val promptDrafts = mutableMapOf<String, String>()

    fun onPromptChanged(newPrompt: String) {
        _prompt.value = newPrompt
        _selectedSessionId.value?.let { promptDrafts[it] = newPrompt }
    }

    fun onSizeChanged(size: String) { _selectedSize.value = size }
    fun onQualityChanged(quality: String) { _selectedQuality.value = quality }
    fun onCountChanged(count: Int) { _selectedCount.value = count }
    fun onFormatChanged(f: String) { _selectedFormat.value = f }
    fun onModerationChanged(m: String) { _selectedModeration.value = m }
    fun setImageModel(model: String) {
        viewModelScope.launch { settingsRepository.setImageModel(model) }
    }

    fun selectSession(sessionId: String) {
        _selectedSessionId.value?.let { oldId -> promptDrafts[oldId] = _prompt.value }
        _selectedSessionId.value = sessionId
        _prompt.value = promptDrafts[sessionId] ?: ""
        _displayImages.value = emptyList()
        _selectedPresetId.value = null
    }

    fun generateImage() {
        val sessionId = _selectedSessionId.value ?: return
        val currentPromptText = _prompt.value
        if (currentPromptText.isBlank()) return
        
        generationJob = viewModelScope.launch {
            _isGenerating.value = true
            _errorMessage.value = null
            _elapsedTime.value = 0
            
            val timerJob = launch { while (true) { delay(1000); _elapsedTime.value += 1 } }

            try {
                val apiModel = when(selectedModel.value) {
                    "5.5" -> "dall-e-3"
                    "5.4" -> "dall-e-3"
                    "5.4mini" -> "dall-e-2"
                    else -> "dall-e-3"
                }

                val request = GenerateRequest(
                    prompt = currentPromptText,
                    quality = _selectedQuality.value,
                    size = _selectedSize.value,
                    format = _selectedFormat.value,
                    moderation = _selectedModeration.value,
                    model = apiModel,
                    n = _selectedCount.value,
                    sessionId = sessionId
                )

                val response = ima2GenApi.generate(request)

                if (response.isSuccessful) {
                    val body = response.body()
                    val newImages = mutableListOf<UiGeneratedImage>()
                    
                    body?.images?.forEach { img ->
                        newImages.add(UiGeneratedImage(image = img.image, revisedPrompt = img.revisedPrompt))
                    } ?: body?.image?.let { 
                        newImages.add(UiGeneratedImage(image = it, revisedPrompt = body.revisedPrompt))
                    }

                    _displayImages.value = newImages
                    
                    newImages.forEach { genImage ->
                        if (genImage.image.isNotEmpty()) {
                            historyDao.insertHistory(
                                HistoryEntity(
                                    sessionId = sessionId,
                                    prompt = currentPromptText,
                                    revisedPrompt = genImage.revisedPrompt,
                                    imageUrl = genImage.image
                                )
                            )
                        }
                    }
                } else {
                    _errorMessage.value = "서버 오류: ${response.code()} ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "이미지 생성 실패: ${e.localizedMessage}"
            } finally {
                timerJob.cancel()
                _isGenerating.value = false
            }
        }
    }

    fun cancelGeneration() {
        generationJob?.cancel()
        _isGenerating.value = false
    }

    fun dismissError() {
        _errorMessage.value = null
    }

    fun createSession(name: String) {
        viewModelScope.launch {
            val newSession = SessionEntity(projectId = projectId, name = name)
            historyDao.insertSession(newSession)
            selectSession(newSession.id)
        }
    }

    fun renameSession(id: String, newName: String) {
        viewModelScope.launch {
            historyDao.updateSessionName(id, newName)
        }
    }

    fun deleteSession(id: String) {
        viewModelScope.launch {
            historyDao.deleteSession(id)
            if (_selectedSessionId.value == id) {
                _selectedSessionId.value = null
                _prompt.value = ""
            }
        }
    }

    fun savePreset(name: String, presetPrompt: String) {
        viewModelScope.launch {
            historyDao.insertPreset(PromptPresetEntity(name = name, content = presetPrompt))
        }
    }

    fun deletePreset(id: String) {
        viewModelScope.launch { historyDao.deletePreset(id) }
    }

    fun selectPreset(preset: PromptPresetEntity?) {
        _selectedPresetId.value = preset?.id
        preset?.let { _prompt.value = it.content }
    }

    fun selectHistoryItem(item: HistoryEntity) {
        _displayImages.value = listOf(UiGeneratedImage(image = item.imageUrl, revisedPrompt = item.revisedPrompt))
    }

    private fun calculateCost(model: String, size: String, quality: String, imageCount: Int): Double {
        val basePrice = when {
            model == "5.5" -> 0.080
            model == "5.4" -> 0.040
            else -> 0.020
        }
        val sizeMultiplier = when {
            size.contains("4096") || size.contains("3840") -> 4.0
            size.contains("2048") -> 2.0
            else -> 1.0
        }
        val qualityMultiplier = if (quality == "hd") 2.0 else 1.0
        return basePrice * sizeMultiplier * qualityMultiplier * imageCount
    }
}
