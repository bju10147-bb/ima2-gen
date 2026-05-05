package com.ima2gen.app.ui.generate

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.ima2gen.app.ui.components.SessionSelector
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateScreen(
    viewModel: GenerateViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onNavigateToGallery: () -> Unit = {}
) {
    val prompt by viewModel.prompt.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val displayImages by viewModel.displayImages.collectAsState()
    val sessionHistory by viewModel.sessionHistory.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val elapsedTime by viewModel.elapsedTime.collectAsState()
    val selectedModel by viewModel.selectedModel.collectAsState()
    val selectedSize by viewModel.selectedSize.collectAsState()
    val selectedQuality by viewModel.selectedQuality.collectAsState()
    val selectedCount by viewModel.selectedCount.collectAsState()
    val selectedFormat by viewModel.selectedFormat.collectAsState()
    val selectedModeration by viewModel.selectedModeration.collectAsState()
    val sessions by viewModel.sessions.collectAsState()
    val selectedSessionId by viewModel.selectedSessionId.collectAsState()
    val estimatedCost by viewModel.estimatedCost.collectAsState()
    val presets by viewModel.presets.collectAsState()
    val selectedPresetId by viewModel.selectedPresetId.collectAsState()

    var fullScreenImageUrl by remember { mutableStateOf<String?>(null) }

    if (fullScreenImageUrl != null) {
        FullScreenImageDialog(
            imageUrl = fullScreenImageUrl!!,
            onDismiss = { fullScreenImageUrl = null }
        )
    }

    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = viewModel::dismissError,
            title = { Text("OpenAI 오류") },
            text = { Text(errorMessage!!) },
            confirmButton = { TextButton(onClick = viewModel::dismissError) { Text("확인") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("이미지 생성") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    IconButton(onClick = onNavigateToGallery) {
                        Icon(Icons.Filled.Collections, contentDescription = "Gallery")
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
            // ── Compact Action Bar (Matches Screenshot) ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Count Select (Small)
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.width(80.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .clickable { /* Show count dropdown */ }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.FilterNone, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(selectedCount.toString(), style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                    }
                }

                // Preset Select (Small)
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier
                            .clickable { /* Show preset dropdown */ }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.AutoFixHigh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = presets.find { it.id == selectedPresetId }?.name ?: "기본",
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                    }
                }

                // Session Selector (Small)
                Box(modifier = Modifier.width(100.dp)) {
                    SessionSelector(
                        sessions = sessions,
                        selectedSessionId = selectedSessionId,
                        onSessionSelected = viewModel::selectSession,
                        onCreateSession = viewModel::createSession,
                        onDeleteSession = viewModel::deleteSession,
                        onRenameSession = viewModel::renameSession,
                        isCompact = true
                    )
                }
            }

            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Prompt Area
                OutlinedTextField(
                    value = prompt,
                    onValueChange = viewModel::onPromptChanged,
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    label = { Text("프롬프트 입력") },
                    placeholder = { Text("이미지를 묘사해보세요...") },
                    maxLines = 10,
                    enabled = !isGenerating,
                    shape = MaterialTheme.shapes.medium
                )

                // Detailed Options (Expandable or always visible)
                if (!isGenerating) {
                    GenerationOptionsSection(
                        selectedModel = selectedModel, onModelSelected = viewModel::setImageModel,
                        selectedSize = selectedSize, onSizeSelected = viewModel::onSizeChanged,
                        selectedQuality = selectedQuality, onQualitySelected = viewModel::onQualityChanged,
                        selectedCount = selectedCount, onCountSelected = viewModel::onCountChanged,
                        selectedFormat = selectedFormat, onFormatSelected = viewModel::onFormatChanged,
                        selectedModeration = selectedModeration, onModerationSelected = viewModel::onModerationChanged
                    )
                }

                // Generation Action
                if (isGenerating) {
                    Button(onClick = viewModel::cancelGeneration, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                        Icon(Icons.Filled.Cancel, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                        Text("생성 중지 ($elapsedTime 초)")
                    }
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                } else {
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Filled.Payments, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                            Text(text = "예상 비용: $${String.format("%.3f", estimatedCost)}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = viewModel::generateImage,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            enabled = prompt.isNotBlank() && selectedSessionId != null,
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                            Text("이미지 생성", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }

                // Results
                if (displayImages.isNotEmpty()) {
                    Text("생성 결과", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    displayImages.forEach { genImage ->
                        MainImageCard(
                            genImage = genImage, 
                            size = selectedSize,
                            onImageClick = { fullScreenImageUrl = genImage.image }
                        )
                    }
                }

                // History
                if (sessionHistory.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("세션 히스토리", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(sessionHistory) { item ->
                                val isSelected = displayImages.any { it.image == item.imageUrl }
                                Card(
                                    modifier = Modifier
                                        .size(90.dp)
                                        .border(
                                            width = if (isSelected) 3.dp else 0.dp,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                            shape = MaterialTheme.shapes.small
                                        )
                                        .clickable { viewModel.selectHistoryItem(item) },
                                    shape = MaterialTheme.shapes.small
                                ) {
                                    coil.compose.AsyncImage(
                                        model = item.imageUrl,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

private fun Modifier.size(size: Int): Modifier = this.size(size.dp)

@Composable
fun FullScreenImageDialog(imageUrl: String, onDismiss: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
    ) {
        var scale by remember { mutableStateOf(1f) }
        var offset by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f))
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(1f, 5f)
                        if (scale > 1f) {
                            offset += pan
                        } else {
                            offset = androidx.compose.ui.geometry.Offset.Zero
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            coil.compose.AsyncImage(
                model = imageUrl, contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(scaleX = scale, scaleY = scale, translationX = offset.x, translationY = offset.y),
                contentScale = ContentScale.Fit
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 40.dp, start = 20.dp, end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(32.dp))
                }
                
                IconButton(
                    onClick = {
                        scope.launch {
                            val result = com.ima2gen.app.util.ImageSaver.saveImageToGallery(context, imageUrl)
                            if (result.isSuccess) {
                                android.widget.Toast.makeText(context, "갤러리에 저장되었습니다.", android.widget.Toast.LENGTH_SHORT).show()
                            } else {
                                android.widget.Toast.makeText(context, "저장 실패: ${result.exceptionOrNull()?.message}", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                ) {
                    Icon(Icons.Filled.Download, contentDescription = "Save", tint = Color.White, modifier = Modifier.size(32.dp))
                }
            }
        }
    }
}

@Composable
fun MainImageCard(genImage: UiGeneratedImage, size: String, onImageClick: () -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }
    val aspectRatio = remember(size) {
        val parts = size.split("x")
        (parts.getOrNull(0)?.toFloatOrNull() ?: 1024f) / (parts.getOrNull(1)?.toFloatOrNull() ?: 1024f)
    }
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth().clickable { onImageClick() },
            shape = MaterialTheme.shapes.medium, 
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            coil.compose.AsyncImage(
                model = genImage.image,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().aspectRatio(aspectRatio),
                contentScale = ContentScale.Fit
            )
        }
        if (!genImage.revisedPrompt.isNullOrBlank()) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp).clickable { isExpanded = !isExpanded }
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = if (isExpanded) "수정된 프롬프트 (전체):" else "수정된 프롬프트 (클릭하여 보기):", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = genImage.revisedPrompt, style = MaterialTheme.typography.bodySmall, maxLines = if (isExpanded) Int.MAX_VALUE else 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerationOptionsSection(
    selectedModel: String, onModelSelected: (String) -> Unit,
    selectedSize: String, onSizeSelected: (String) -> Unit,
    selectedQuality: String, onQualitySelected: (String) -> Unit,
    selectedCount: Int, onCountSelected: (Int) -> Unit,
    selectedFormat: String, onFormatSelected: (String) -> Unit,
    selectedModeration: String, onModerationSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val modelOptions = listOf("gpt-5.5", "gpt-5.4", "gpt-5.4-mini")
            OptionDropdown(label = "모델", options = modelOptions, selectedOption = selectedModel, onOptionSelected = onModelSelected, modifier = Modifier.weight(1f))

            val qualityOptions = listOf("high", "medium", "low")
            OptionDropdown(label = "품질", options = qualityOptions, selectedOption = selectedQuality, onOptionSelected = onQualitySelected, modifier = Modifier.weight(1f))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val sizes = listOf(
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
                "auto",
            )
            OptionDropdown(label = "해상도", options = sizes, selectedOption = selectedSize, onOptionSelected = onSizeSelected, modifier = Modifier.weight(1f))

            val countOptions = (1..8).map { it.toString() }
            OptionDropdown(label = "생성 수", options = countOptions, selectedOption = selectedCount.toString(), onOptionSelected = { onCountSelected(it.toInt()) }, modifier = Modifier.weight(1f))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OptionDropdown(label = "포맷", options = listOf("png", "webp", "jpeg"), selectedOption = selectedFormat, onOptionSelected = onFormatSelected, modifier = Modifier.weight(1f))
            OptionDropdown(label = "검수", options = listOf("low", "auto"), selectedOption = selectedModeration, onOptionSelected = onModerationSelected, modifier = Modifier.weight(1f))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionDropdown(
    label: String, options: List<String>, descriptions: List<String>? = null,
    selectedOption: String, onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier, enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded && enabled, onExpandedChange = { if (enabled) expanded = !expanded }, modifier = modifier) {
        OutlinedTextField(
            value = selectedOption.uppercase(), onValueChange = {}, readOnly = true, label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier.menuAnchor().fillMaxWidth(), enabled = enabled, textStyle = MaterialTheme.typography.bodySmall
        )
        ExposedDropdownMenu(expanded = expanded && enabled, onDismissRequest = { expanded = false }) {
            options.forEachIndexed { i, opt ->
                DropdownMenuItem(
                    text = { Column {
                        Text(opt.uppercase(), fontWeight = if (opt == selectedOption) FontWeight.Bold else FontWeight.Normal)
                        if (descriptions != null && i < descriptions.size) Text(descriptions[i], style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } },
                    onClick = { onOptionSelected(opt); expanded = false }
                )
            }
        }
    }
}
