package com.ima2gen.app.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ima2gen.app.data.repository.AppLanguage
import com.ima2gen.app.data.repository.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val imageModel by viewModel.imageModel.collectAsState()
    val theme by viewModel.theme.collectAsState()
    val language by viewModel.language.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("설정") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // ── Image Model Section ──
            SettingsSection(title = "이미지 엔진 모델", icon = Icons.Default.AutoAwesome) {
                val models = listOf("5.4mini", "5.4", "5.5")
                models.forEach { model ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (imageModel == model),
                            onClick = { viewModel.setImageModel(model) }
                        )
                        Text(
                            text = model,
                            modifier = Modifier.padding(start = 8.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        if (model == "5.5") {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.extraSmall
                            ) {
                                Text(
                                    "NEW",
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }

            // ── Theme Section ──
            SettingsSection(title = "테마 설정", icon = Icons.Default.Palette) {
                AppTheme.entries.forEach { t ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (theme == t),
                            onClick = { viewModel.setTheme(t) }
                        )
                        Text(
                            text = when(t) {
                                AppTheme.SYSTEM -> "시스템 설정"
                                AppTheme.LIGHT -> "라이트 모드"
                                AppTheme.DARK -> "다크 모드"
                            },
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            // ── Language Section ──
            SettingsSection(title = "언어 (Language)", icon = Icons.Default.Language) {
                AppLanguage.entries.forEach { lang ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (language == lang),
                            onClick = { viewModel.setLanguage(lang) }
                        )
                        Text(
                            text = when(lang) {
                                AppLanguage.SYSTEM -> "시스템 기본값"
                                AppLanguage.KO -> "한국어 (Korean)"
                                AppLanguage.EN -> "English"
                                AppLanguage.JA -> "日本語 (Japanese)"
                                AppLanguage.ZH -> "中文 (Chinese)"
                            },
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            // ── Security & AI Policy Section ──
            SettingsSection(title = "보안 및 AI 정책", icon = Icons.Default.Security) {
                Text(
                    text = "• 보안: 사용자의 API Key는 서버로 전송되거나 저장되지 않으며, 안드로이드 보안 영역(Keystore)에 암호화되어 로컬에만 유지됩니다.\n" +
                           "• 정책: OpenAI의 Usage Policy를 준수하며, 부적절한 이미지 생성 시 서비스 이용이 제한될 수 있습니다.\n" +
                           "• 데이터: 생성된 이미지는 사용자의 설정된 프로젝트 폴더에만 저장됩니다.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // ── Legal & License Section ──
            var showLicenseDialog by remember { mutableStateOf(false) }
            if (showLicenseDialog) {
                AlertDialog(
                    onDismissRequest = { showLicenseDialog = false },
                    title = { Text("MIT License") },
                    text = {
                        Box(modifier = Modifier.heightIn(max = 400.dp).verticalScroll(rememberScrollState())) {
                            Text(
                                text = """
                                    MIT License

                                    Copyright (c) 2026 Ima2-Gen Contributors

                                    Permission is hereby granted, free of charge, to any person obtaining a copy
                                    of this software and associated documentation files (the "Software"), to deal
                                    in the Software without restriction, including without limitation the rights
                                    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
                                    copies of the Software, and to permit persons to whom the Software is
                                    furnished to do so, subject to the following conditions:

                                    The above copyright notice and this permission notice shall be included in all
                                    copies or substantial portions of the Software.

                                    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
                                    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
                                    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
                                    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
                                    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
                                    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
                                    SOFTWARE.
                                """.trimIndent(),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showLicenseDialog = false }) { Text("닫기") }
                    }
                )
            }

            SettingsSection(title = "법적 고지 및 라이선스", icon = Icons.Default.Gavel) {
                Column {
                    Text(
                        text = "Copyright (c) 2026 Ima2-Gen Contributors. 본 앱의 소스코드는 MIT 라이선스를 따릅니다.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(
                        onClick = { showLicenseDialog = true },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("라이선스 전문 보기", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Ima2-Gen v1.0.0",
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                content()
            }
        }
    }
}

