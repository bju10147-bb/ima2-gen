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
    val referenceImages by viewModel.referenceImages.collectAsState()
    val sessions by viewModel.sessions.collectAsState()
    val selectedSessionId by viewModel.selectedSessionId.collectAsState()

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
            SessionSelector(
                sessions = sessions,
                selectedSessionId = selectedSessionId,
                onSessionSelected = viewModel::selectSession,
                onCreateSession = viewModel::createSession,
                onDeleteSession = viewModel::deleteSession
            )

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

                // ── Generation Options (DROPDOWNS) ──
                if (!isGenerating) {
                    GenerationOptionsSection(
                        selectedModel = selectedModel,
                        onModelSelected = viewModel::onModelChanged,
                        selectedSize = selectedSize,
                        onSizeSelected = viewModel::onSizeChanged,
                        selectedQuality = selectedQuality,
                        onQualitySelected = viewModel::onQualityChanged,
                        selectedCount = selectedCount,
                        onCountSelected = viewModel::onCountChanged
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
                        val aspectRatio = remember(selectedSize) {
                            val parts = selectedSize.split("x")
                            val w = parts.getOrNull(0)?.toFloatOrNull() ?: 1024f
                            val h = parts.getOrNull(1)?.toFloatOrNull() ?: 1024f
                            w / h
                        }

                        val imageModel = remember(genImage.image) {
                            try {
                                if (genImage.image.startsWith("data:image")) {
                                    val b64 = genImage.image.substringAfter("base64,")
                                    Base64.decode(b64, Base64.DEFAULT)
                                } else {
                                    genImage.image
                                }
                            } catch (e: Exception) {
                                null
                            }
                        }

                        if (imageModel != null) {
                            Column {
                                coil.compose.AsyncImage(
                                    model = imageModel,
                                    contentDescription = "Generated Image",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(aspectRatio)
                                        .clip(MaterialTheme.shapes.medium),
                                    contentScale = ContentScale.Fit
                                )
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val context = androidx.compose.ui.platform.LocalContext.current
                                    val scope = androidx.compose.runtime.rememberCoroutineScope()

                                    IconButton(onClick = { 
                                        scope.launch { com.ima2gen.app.util.ImageActionHelper.shareImage(context, genImage.image) }
                                    }) {
                                        Icon(Icons.Filled.Share, contentDescription = "Share", modifier = Modifier.size(20.dp))
                                    }
                                    IconButton(onClick = { 
                                        scope.launch { com.ima2gen.app.util.ImageActionHelper.downloadImage(context, genImage.image) }
                                    }) {
                                        Icon(Icons.Filled.Download, contentDescription = "Download", modifier = Modifier.size(20.dp))
                                    }
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .clip(MaterialTheme.shapes.medium),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Image, contentDescription = null, modifier = Modifier.size(48.dp))
                                Text("이미지 로드 실패")
                            }
                        }
                        
                        if (!genImage.revisedPrompt.isNullOrBlank()) {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = MaterialTheme.shapes.small,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "수정된 프롬프트:\n${genImage.revisedPrompt}",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
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
    onCountSelected: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // ── Model & Quality Row ──
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OptionDropdown(
                label = "모델",
                options = listOf("dall-e-3", "dall-e-2"),
                selectedOption = selectedModel,
                onOptionSelected = onModelSelected,
                modifier = Modifier.weight(1f)
            )
            OptionDropdown(
                label = "품질",
                options = if (selectedModel == "dall-e-3") listOf("standard", "hd") else listOf("standard"),
                selectedOption = selectedQuality,
                onOptionSelected = onQualitySelected,
                modifier = Modifier.weight(1f),
                enabled = selectedModel == "dall-e-3"
            )
        }

        // ── Size & Count Row ──
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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionDropdown(
    label: String,
    options: List<String>,
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
            enabled = enabled
        )
        
        ExposedDropdownMenu(
            expanded = expanded && enabled,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { 
                        Text(
                            text = when(option) {
                                "1024x1024" -> "1:1 Square"
                                "1792x1024" -> "16:9 Wide"
                                "1024x1792" -> "9:16 Tall"
                                else -> option.uppercase()
                            }
                        ) 
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
