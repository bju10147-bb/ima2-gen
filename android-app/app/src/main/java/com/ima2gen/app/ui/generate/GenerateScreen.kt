package com.ima2gen.app.ui.generate

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateScreen(
    viewModel: GenerateViewModel = hiltViewModel(),
) {
    val prompt by viewModel.prompt.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val generatedImages by viewModel.generatedImages.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val elapsedTime by viewModel.elapsedTime.collectAsState()

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
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = prompt,
                onValueChange = viewModel::onPromptChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                label = { Text("프롬프트 입력") },
                placeholder = { Text("원하는 이미지를 자세히 묘사해보세요...") },
                maxLines = 5,
                enabled = !isGenerating
            )

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
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Button(
                    onClick = viewModel::generateImage,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = prompt.isNotBlank()
                ) {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                    Text("이미지 생성")
                }
            }

            if (generatedImages.isNotEmpty() && !isGenerating) {
                Text(
                    text = "생성 결과",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 16.dp)
                )
                
                generatedImages.forEach { genImage ->
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
                        coil.compose.AsyncImage(
                            model = imageModel,
                            contentDescription = "Generated Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(MaterialTheme.shapes.medium),
                            contentScale = ContentScale.Crop
                        )
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
                }
            }
        }
    }
}
