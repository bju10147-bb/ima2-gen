package com.ima2gen.app.ui.generate

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ima2gen.app.data.api.OpenAiApi
import com.ima2gen.app.data.api.ResponsesContentItem
import com.ima2gen.app.data.api.ResponsesImageRequest
import com.ima2gen.app.data.api.ResponsesInputMessage
import com.ima2gen.app.data.api.ResponsesReasoning
import com.ima2gen.app.data.api.ResponsesTool
import com.ima2gen.app.data.local.db.HistoryDao
import com.ima2gen.app.data.local.db.HistoryEntity
import com.ima2gen.app.data.local.db.PromptPresetEntity
import com.ima2gen.app.data.local.db.SessionEntity
import com.ima2gen.app.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UiGeneratedImage(
    val image: String,
    val revisedPrompt: String? = null
)

private data class ImageQualityProfile(
    val responseModel: String = "gpt-5.5",
    val reasoningEffort: String = "low",
    val imageQuality: String = "high",
    val outputFormat: String = "png",
    val background: String = "auto",
    val webSearchEnabled: Boolean = true,
)

@HiltViewModel
class GenerateViewModel @Inject constructor(
    private val openAiApi: OpenAiApi,
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

    private val _selectedQuality = MutableStateFlow("high")
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
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "gpt-5.5")

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
                val profile = buildQualityProfile(
                    modelAlias = selectedModel.value,
                    quality = _selectedQuality.value,
                    outputFormat = _selectedFormat.value,
                )
                val requestedCount = _selectedCount.value
                val effectiveSize = normalizeResponsesSize(_selectedSize.value)
                val requestProfile = buildRequestProfile(
                    profile = profile,
                    size = effectiveSize,
                    moderation = _selectedModeration.value,
                )
                val newImages = mutableListOf<UiGeneratedImage>()
                var lastError: String? = null

                val responses = coroutineScope {
                    val deferredResults = (1..requestedCount).map {
                        async {
                            try {
                                openAiApi.createResponse(
                                    buildResponsesRequest(
                                        prompt = currentPromptText,
                                        profile = profile,
                                        size = effectiveSize,
                                        moderation = _selectedModeration.value,
                                    )
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                                lastError = e.localizedMessage ?: e.javaClass.simpleName
                                null
                            }
                        }
                    }
                    deferredResults.awaitAll()
                }

                responses.forEach { response ->
                    if (response != null && response.isSuccessful) {
                        val generated = extractGeneratedImage(response.body(), profile.outputFormat)
                        if (generated != null) {
                            newImages.add(generated)
                            historyDao.insertHistory(
                                HistoryEntity(
                                    sessionId = sessionId,
                                    prompt = currentPromptText,
                                    revisedPrompt = generated.revisedPrompt,
                                    imageUrl = generated.image,
                                    requestProfile = requestProfile,
                                )
                            )
                        }
                    } else if (response != null) {
                        lastError = "${response.code()} ${response.errorBody()?.string()}"
                    }
                }

                if (newImages.isNotEmpty()) {
                    _displayImages.value = newImages
                    if (lastError != null && newImages.size < requestedCount) {
                        // Some succeeded, some failed
                        _errorMessage.value = "일부 이미지 생성 실패: $lastError"
                    }
                } else {
                    _errorMessage.value = lastError?.let { "OpenAI 오류: $it" } ?: "이미지 생성에 실패했습니다."
                }
            } catch (e: Exception) {
                _errorMessage.value = "이미지 생성 실패: ${e.localizedMessage}"
            } finally {
                timerJob.cancel()
                _isGenerating.value = false
            }
        }
    }

    private fun buildQualityProfile(
        modelAlias: String,
        quality: String,
        outputFormat: String,
    ): ImageQualityProfile {
        val responseModel = when (modelAlias) {
            "gpt-5.5", "gpt-5.4", "gpt-5.4-mini" -> modelAlias
            "5.5" -> "gpt-5.5"
            "5.4" -> "gpt-5.4"
            "5.4mini", "gpt-5", "gpt-4.1" -> "gpt-5.5"
            else -> "gpt-5.5"
        }
        val normalizedQuality = when (quality) {
            "low", "medium", "high", "auto" -> quality
            else -> "high"
        }
        val normalizedFormat = when (outputFormat) {
            "png", "webp", "jpeg" -> outputFormat
            else -> "png"
        }
        return ImageQualityProfile(
            responseModel = responseModel,
            imageQuality = normalizedQuality,
            outputFormat = normalizedFormat,
        )
    }

    private fun buildResponsesRequest(
        prompt: String,
        profile: ImageQualityProfile,
        size: String,
        moderation: String,
    ): ResponsesImageRequest {
        val tools = buildList {
            if (profile.webSearchEnabled) add(ResponsesTool(type = "web_search"))
            add(
                ResponsesTool(
                    type = "image_generation",
                    quality = profile.imageQuality,
                    size = size,
                    moderation = normalizeModeration(moderation),
                    outputFormat = profile.outputFormat,
                    background = profile.background,
                )
            )
        }
        return ResponsesImageRequest(
            model = profile.responseModel,
            input = listOf(
                ResponsesInputMessage(
                    role = "developer",
                    content = listOf(ResponsesContentItem(type = "input_text", text = GENERATE_DEVELOPER_PROMPT)),
                ),
                ResponsesInputMessage(
                    role = "user",
                    content = listOf(ResponsesContentItem(type = "input_text", text = buildUserPrompt(prompt))),
                ),
            ),
            tools = tools,
            reasoning = ResponsesReasoning(effort = profile.reasoningEffort),
            stream = false,
        )
    }

    private fun buildUserPrompt(prompt: String): String {
        return "Generate an image: $prompt\n\n$PROMPT_FIDELITY_SUFFIX"
    }

    private fun buildRequestProfile(
        profile: ImageQualityProfile,
        size: String,
        moderation: String,
    ): String {
        return listOf(
            "api=responses",
            "model=${profile.responseModel}",
            "tool=image_generation",
            "quality=${profile.imageQuality}",
            "size=$size",
            "format=${profile.outputFormat}",
            "background=${profile.background}",
            "moderation=${normalizeModeration(moderation)}",
            "reasoning=${profile.reasoningEffort}",
            "webSearch=${profile.webSearchEnabled}",
            "promptMode=preserve",
        ).joinToString(";")
    }

    private fun extractGeneratedImage(
        response: com.ima2gen.app.data.api.ResponsesImageResponse?,
        outputFormat: String,
    ): UiGeneratedImage? {
        val item = response?.output
            ?.firstOrNull { it.type == "image_generation_call" && !it.result.isNullOrBlank() }
            ?: return null
            
        val result = item.result!!
        val finalImage = when {
            result.startsWith("data:") -> result
            result.startsWith("http") -> result
            // Check if it's potentially a JSON string (sometimes tool outputs are wrapped)
            result.startsWith("{") && result.contains("url") -> {
                // Crude extraction if it's a JSON string
                val regex = "\"url\"\\s*:\\s*\"([^\"]+)\"".toRegex()
                regex.find(result)?.groupValues?.get(1) ?: "data:image/${outputFormat};base64,$result"
            }
            result.startsWith("{") && result.contains("b64_json") -> {
                val regex = "\"b64_json\"\\s*:\\s*\"([^\"]+)\"".toRegex()
                val b64 = regex.find(result)?.groupValues?.get(1) ?: result
                "data:image/${outputFormat};base64,$b64"
            }
            else -> "data:image/${outputFormat};base64,$result"
        }
        
        return UiGeneratedImage(
            image = finalImage,
            revisedPrompt = item.revisedPrompt,
        )
    }

    private fun normalizeResponsesSize(size: String): String {
        return when (size) {
            "1024x1024",
            "1536x1024",
            "1024x1536",
            "1360x1024",
            "1024x1360",
            "1824x1024",
            "1024x1824",
            "2048x2048",
            "2048x1152",
            "1152x2048",
            "3840x2160",
            "2160x3840",
            "auto" -> size
            else -> "1024x1024"
        }
    }

    private fun normalizeModeration(moderation: String): String {
        return when (moderation) {
            "auto", "low" -> moderation
            else -> "low"
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
            quality == "high" && size == "1024x1024" -> 0.211
            quality == "high" && size in setOf("1024x1536", "1536x1024", "1024x1360", "1360x1024") -> 0.165
            quality == "high" && size in setOf("1024x1824", "1824x1024") -> 0.200
            quality == "high" && size == "2048x2048" -> 0.422
            quality == "high" && size in setOf("2048x1152", "1152x2048") -> 0.320
            quality == "high" && size in setOf("3840x2160", "2160x3840") -> 0.800
            quality == "medium" && size == "1024x1024" -> 0.053
            quality == "medium" && size in setOf("1024x1536", "1536x1024", "1024x1360", "1360x1024") -> 0.041
            quality == "medium" && size in setOf("1024x1824", "1824x1024") -> 0.050
            quality == "medium" && size == "2048x2048" -> 0.106
            quality == "medium" && size in setOf("2048x1152", "1152x2048") -> 0.080
            quality == "medium" && size in setOf("3840x2160", "2160x3840") -> 0.200
            quality == "low" && size == "1024x1024" -> 0.006
            quality == "low" && size in setOf("1024x1536", "1536x1024", "1024x1360", "1360x1024") -> 0.005
            quality == "low" && size in setOf("1024x1824", "1824x1024") -> 0.006
            quality == "low" && size == "2048x2048" -> 0.012
            quality == "low" && size in setOf("2048x1152", "1152x2048") -> 0.009
            quality == "low" && size in setOf("3840x2160", "2160x3840") -> 0.023
            else -> 0.211
        }
        return basePrice * imageCount
    }

    companion object {
        private const val PROMPT_FIDELITY_SUFFIX =
            "When you call the image_generation tool, treat the user's prompt as the source of truth. " +
                "If the prompt is already visually sufficient, pass it through unchanged as the image_generation prompt argument. " +
                "Do not translate, summarize, rewrite, restyle, expand, or add descriptors unless genuinely necessary to satisfy an underspecified visual request. " +
                "If the user wrote in Korean, keep the Korean text. Do not inject additional style descriptors when the user already specified a style."

        private const val GENERATE_DEVELOPER_PROMPT =
            "You are an image generation assistant. Your primary function is to invoke the image_generation tool. Never respond with plain text only. " +
                "Preserve the user's prompt by default. If the prompt is visually sufficient, pass it through unchanged as the image_generation prompt argument. " +
                "Use web_search only when factual visual accuracy is genuinely required and the user's prompt is insufficient; then append only concrete visual facts after the user's original prompt. " +
                "Quality guidelines: crisp details, clean lines, well-balanced composition, appropriate contrast and color. " +
                "Avoid blur, noise, compression artifacts, watermark, signature, cropped elements, and duplicates. " +
                "Text and typography must be rendered with precise spelling, sharp edges, and no distortion. " +
                "Preserve the style the user explicitly or implicitly requests. If no style is specified, produce a polished, high-quality image without imposing photorealism."
    }
}
