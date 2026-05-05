package com.ima2gen.app.ui.settings

import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ima2gen.app.data.repository.AppLanguage
import com.ima2gen.app.data.repository.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onLogout: () -> Unit = {}
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
            // ── Account & Security Section ──
            var showLogoutDialog by remember { mutableStateOf(false) }
            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false },
                    title = { Text("API 키 초기화") },
                    text = { Text("저장된 API 키를 삭제하고 인증 화면으로 돌아가시겠습니까?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.resetApiKey()
                                showLogoutDialog = false
                                onLogout()
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) { Text("초기화") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showLogoutDialog = false }) { Text("취소") }
                    }
                )
            }

            SettingsSection(title = "계정 및 보안", icon = Icons.Default.VpnKey) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("OpenAI API 키 설정", style = MaterialTheme.typography.bodyMedium)
                    TextButton(onClick = { showLogoutDialog = true }) {
                        Text("초기화 및 로그아웃", color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            // ── AI Model Guide Section (New) ──
            SettingsSection(title = "AI 모델 가이드", icon = Icons.Default.HelpCenter) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ModelGuideItem(
                        title = "GPT-5.5 + image_generation",
                        description = "PC API Provider와 같은 Responses API 이미지 생성 경로를 사용합니다. 공식 이미지 도구는 GPT Image 2 계열을 사용하며 앱에서 별도 번역이나 프롬프트 재작성은 하지 않습니다."
                    )
                    ModelGuideItem(
                        title = "고품질 고정 프로필",
                        description = "기본값은 gpt-5.5, high 품질, PNG 출력, 원본 프롬프트 보존입니다. PC와 앱을 비교할 때 모델/품질/해상도/포맷을 반드시 동일하게 맞추세요."
                    )
                    ModelGuideItem(
                        title = "프롬프트 보존",
                        description = "앱은 한글 프롬프트를 임의 번역하지 않습니다. 모델의 공식 image_generation 도구가 최종 이미지를 생성합니다."
                    )
                }
            }

            // ── Image Model Selection Section ──
            SettingsSection(title = "기본 생성 모델 설정", icon = Icons.Default.AutoAwesome) {
                val models = listOf("gpt-5.5", "gpt-5.4", "gpt-5.4-mini")
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
                            text = model.uppercase(),
                            modifier = Modifier.padding(start = 8.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        if (model == "gpt-5.5") {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.extraSmall
                            ) {
                                Text(
                                    "BEST",
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
                    text = "• 보안: 사용자의 API Key는 외부 서버로 전송되지 않으며, 안드로이드 보안 영역(Keystore)에 암호화되어 로컬에만 유지됩니다.\n" +
                           "• 직접 통신: 본 앱은 프록시 서버 없이 OpenAI API와 직접 통신하는 단독형 앱입니다.\n" +
                           "• 품질 일치: PC와 같은 결과 품질을 원하면 PC도 API Provider/Responses image_generation 경로에서 동일한 모델·품질·해상도·포맷·웹검색 조건을 사용해야 합니다.\n" +
                           "• 면책: AI 모델이 생성하는 결과물은 항상 정확하거나 적절하지 않을 수 있습니다.\n" +
                           "• 정책: OpenAI의 Usage Policy를 준수해야 하며, 부적절한 용도로의 사용을 금지합니다.",
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
                        TextButton(onClick = { showLicenseDialog = true }) { Text("닫기") }
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
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Ima2-Gen Standalone v1.1.0",
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ModelGuideItem(title: String, description: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
