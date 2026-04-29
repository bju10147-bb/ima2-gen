package com.ima2gen.app.ui.generate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ima2gen.app.data.api.OpenAiApi
import com.ima2gen.app.data.api.OpenAiImageRequest
import com.ima2gen.app.data.api.dto.GeneratedImage
import com.ima2gen.app.data.local.db.HistoryDao
import com.ima2gen.app.data.local.db.HistoryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class GenerateViewModel @Inject constructor(
    private val api: OpenAiApi,
    private val historyDao: HistoryDao,
) : ViewModel() {

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

    fun onPromptChanged(newPrompt: String) {
        _prompt.value = newPrompt
    }

    fun generateImage() {
        if (_prompt.value.isBlank()) return
        
        viewModelScope.launch {
            _isGenerating.value = true
            _errorMessage.value = null
            _generatedImages.value = emptyList()
            _elapsedTime.value = 0

            // Start a timer for the UI
            val timerJob = launch {
                while (_isGenerating.value) {
                    delay(1000)
                    _elapsedTime.value += 1
                }
            }

            try {
                // Mock delay to simulate network request
                delay(3000)

                // Mock response
                val mockImageUrl = "https://picsum.photos/seed/${_prompt.value.hashCode()}/1024/1024"
                val mockRevisedPrompt = "Mock revised prompt for: ${_prompt.value}"

                _generatedImages.value = listOf(
                    GeneratedImage(
                        image = mockImageUrl,
                        revisedPrompt = mockRevisedPrompt
                    )
                )

                // Save to History
                historyDao.insertHistory(
                    HistoryEntity(
                        id = UUID.randomUUID().toString(),
                        prompt = _prompt.value,
                        revisedPrompt = mockRevisedPrompt,
                        imageUrl = mockImageUrl
                    )
                )

                /* --- Real API Call (Commented out for mocking) ---
                val request = OpenAiImageRequest(
                    prompt = _prompt.value
                )
                
                val response = api.generateImage(request)
                
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.data.isNotEmpty()) {
                        _generatedImages.value = body.data.mapNotNull {
                            it.b64Json?.let { b64 -> 
                                GeneratedImage(
                                    image = "data:image/png;base64,$b64",
                                    revisedPrompt = it.revisedPrompt
                                )
                            }
                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.value = "생성 실패: ${response.code()} $errorBody"
                }
                --------------------------------------------------- */
            } catch (e: Exception) {
                _errorMessage.value = "네트워크 오류: ${e.localizedMessage}"
            } finally {
                _isGenerating.value = false
                timerJob.cancel()
            }
        }
    }

    fun cancelGeneration() {
        // Direct OpenAI API calls do not support cancellation natively via a DELETE endpoint.
        _isGenerating.value = false
        _errorMessage.value = "생성이 취소 요청되었습니다 (응답은 무시됨)."
    }
    
    fun dismissError() {
        _errorMessage.value = null
    }
}
