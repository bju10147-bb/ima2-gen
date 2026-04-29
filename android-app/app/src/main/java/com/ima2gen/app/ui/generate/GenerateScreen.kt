package com.ima2gen.app.ui.generate

import android.util.Base64
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ima2gen.app.ui.components.SessionSelector
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateScreen(
    viewModel: GenerateViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val prompt by viewModel.prompt.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val generatedImages by viewModel.generatedImages.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val elapsedTime by viewModel.elapsedTime.collectAsState()
    val selectedModel by viewModel.selectedModel.collectAsState()
    val selectedSize by viewModel.selectedSize.collectAsState()
    val selectedQuality by viewModel.selectedQuality.collectAsState()
    val selectedCount by viewModel.selectedCount.collectAsState()
    val selectedFormat by viewModel.selectedFormat.collectAsState()
    val selectedModeration by viewModel.selectedModeration.collectAsState()
    val referenceImages by viewModel.referenceImages.collectAsState()

    val imagePickerLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetMultipleContents(),
        onResult = { uris -> viewModel.addReferenceImages(uris) }
    )

    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = viewModel::dismissError,
            title = { Text("오류") },
            text = { Text(errorMessage!!) },
            confirmButton = {
                TextButton(onClick = viewModel::dismissError) {
                    Text("확인")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("새 이미지 생성") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val sessions by viewModel.sessions.collectAsState()
            val selectedSessionId by viewModel.selectedSessionId.collectAsState()
            val presets by viewModel.presets.collectAsState()
            val selectedPresetId by viewModel.selectedPresetId.collectAsState()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    SessionSelector(
                        sessions = sessions,
                        selectedSessionId = selectedSessionId,
                        onSessionSelected = viewModel::selectSession,
                        onCreateSession = viewModel::createSession,
                        onDeleteSession = viewModel::deleteSession
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    com.ima2gen.app.ui.components.PromptPresetSelector(
                        presets = presets,
                        selectedPresetId = selectedPresetId,
                        onPresetSelected = viewModel::selectPreset,
                        onCreatePreset = viewModel::createPreset,
                        onDeletePreset = viewModel::deletePreset,
                        currentPrompt = prompt
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ── Reference Images Row ──
                if (referenceImages.isNotEmpty()) {
                    androidx.compose.foundation.lazy.LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(referenceImages.size) { i ->
                            val uri = referenceImages[i]
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(MaterialTheme.shapes.small)
                            ) {
                                coil.compose.AsyncImage(
                                    model = uri,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                IconButton(
                                    onClick = { viewModel.removeReferenceImage(uri) },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(24.dp)
                                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f), MaterialTheme.shapes.extraSmall)
                                ) {
                                    Icon(Icons.Filled.Cancel, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                        if (referenceImages.size < 5) {
                            item {
                                OutlinedCard(
                                    onClick = { imagePickerLauncher.launch("image/*") },
                                    modifier = Modifier.size(80.dp)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Icon(Icons.Filled.AddPhotoAlternate, contentDescription = "Add More")
                                    }
                                }
                            }
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = prompt,
                        onValueChange = viewModel::onPromptChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        label = { Text("프롬프트 입력") },
                        placeholder = { Text("이미지를 묘사하거나 참조 이미지를 활용해보세요...") },
                        maxLines = 5,
                        enabled = !isGenerating,
                        trailingIcon = {
                            if (referenceImages.isEmpty() && !isGenerating) {
                                IconButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                                    Icon(Icons.Filled.AttachFile, contentDescription = "Attach Image")
                                }
                            }
                        }
                    )
                }

                // ── Generation Options ──
                if (!isGenerating) {
                    GenerationOptionsSection(
                        selectedModel = selectedModel,
                        onModelSelected = viewModel::onModelChanged,
                        selectedSize = selectedSize,
                        onSizeSelected = viewModel::onSizeChanged,
                        selectedQuality = selectedQuality,
                        onQualitySelected = viewModel::onQualityChanged,
                        selectedCount = selectedCount,
                        onCountSelected = viewModel::onCountChanged,
                        selectedFormat = selectedFormat,
                        onFormatSelected = viewModel::onFormatChanged,
                        selectedModeration = selectedModeration,
                        onModerationSelected = viewModel::onModerationChanged
                    )
                }

                if (isGenerating) {
                    Button(
                        onClick = viewModel::cancelGeneration,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Filled.Cancel, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                        Text("생성 취소 ($elapsedTime 초)")
                    }
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    Button(
                        onClick = viewModel::generateImage,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = prompt.isNotBlank() && selectedSessionId != null
                    ) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                        Text("이미지 생성 (${selectedCount}장)")
                    }
                }

                if (generatedImages.isNotEmpty() && !isGenerating) {
                    Text(
                        text = "생성 결과",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    
                    generatedImages.forEach { genImage ->
                        // ... (Generated images display remains same)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerationOptionsSection(
    selectedModel: String,
    onModelSelected: (String) -> Unit,
    selectedSize: String,
    onSizeSelected: (String) -> Unit,
    selectedQuality: String,
    onQualitySelected: (String) -> Unit,
    selectedCount: Int,
    onCountSelected: (Int) -> Unit,
    selectedFormat: String,
    onFormatSelected: (String) -> Unit,
    selectedModeration: String,
    onModerationSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // ── Row 1: Model & Quality ──
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            val modelOptions = listOf(
                "dall-e-3" to "최신 모델로 고해상도와 정확한 프롬프트 이해력을 제공합니다.",
                "dall-e-2" to "이전 세대 모델로 더 빠르고 다양한 규격을 지원합니다."
            )
            OptionDropdown(
                label = "모델",
                options = modelOptions.map { it.first },
                descriptions = modelOptions.map { it.second },
                selectedOption = selectedModel,
                onOptionSelected = onModelSelected,
                modifier = Modifier.weight(1f)
            )
            
            val qualityOptions = if (selectedModel == "dall-e-3") {
                listOf(
                    "standard" to "표준 품질로 일반적인 생성에 적합합니다.",
                    "hd" to "고해상도 디테일과 향상된 텍스처를 제공합니다."
                )
            } else {
                listOf("standard" to "표준 품질로 일반적인 생성에 적합합니다.")
            }
            OptionDropdown(
                label = "품질",
                options = qualityOptions.map { it.first },
                descriptions = qualityOptions.map { it.second },
                selectedOption = selectedQuality,
                onOptionSelected = onQualitySelected,
                modifier = Modifier.weight(1f),
                enabled = selectedModel == "dall-e-3"
            )
        }

        // ── Row 2: Size & Count ──
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            val sizes = if (selectedModel == "dall-e-3") {
                listOf("1024x1024", "1792x1024", "1024x1792")
            } else {
                listOf("1024x1024", "512x512", "256x256")
            }
            
            OptionDropdown(
                label = "이미지 규격",
                options = sizes,
                selectedOption = selectedSize,
                onOptionSelected = onSizeSelected,
                modifier = Modifier.weight(1f)
            )
            
            OptionDropdown(
                label = "생성 개수",
                options = listOf(1, 2, 3, 4, 5, 6, 7, 8).map { it.toString() },
                selectedOption = selectedCount.toString(),
                onOptionSelected = { onCountSelected(it.toInt()) },
                modifier = Modifier.weight(1f)
            )
        }

        // ── Row 3: Format & Moderation ──
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OptionDropdown(
                label = "파일 포맷",
                options = listOf("png", "webp", "jpeg"),
                selectedOption = selectedFormat,
                onOptionSelected = onFormatSelected,
                modifier = Modifier.weight(1f)
            )
            
            val modOptions = listOf(
                "auto" to "자동은 표준 안전 필터를 사용합니다.",
                "low" to "낮음은 제한을 조금 완화해 경계선 프롬프트가 더 통과할 수 있게 합니다."
            )
            
            OptionDropdown(
                label = "모데레이션",
                options = modOptions.map { it.first },
                descriptions = modOptions.map { it.second },
                selectedOption = selectedModeration,
                onOptionSelected = onModerationSelected,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionDropdown(
    label: String,
    options: List<String>,
    descriptions: List<String>? = null,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded && enabled,
        onExpandedChange = { if (enabled) expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedOption.uppercase(),
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            enabled = enabled,
            textStyle = MaterialTheme.typography.bodyMedium
        )
        
        ExposedDropdownMenu(
            expanded = expanded && enabled,
            onDismissRequest = { expanded = false }
        ) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(
                    text = { 
                        Column {
                            Text(
                                text = when(option) {
                                    "1024x1024" -> "1:1 Square"
                                    "1792x1024" -> "16:9 Wide"
                                    "1024x1792" -> "9:16 Tall"
                                    "auto" -> "기본 (표준)"
                                    "low" -> "낮음 (완화)"
                                    else -> option.uppercase()
                                },
                                fontWeight = if (option == selectedOption) FontWeight.Bold else FontWeight.Normal
                            )
                            if (descriptions != null && index < descriptions.size) {
                                Text(
                                    text = descriptions[index],
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
