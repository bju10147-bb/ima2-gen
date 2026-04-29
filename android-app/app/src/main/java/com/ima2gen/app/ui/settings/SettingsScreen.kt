package com.ima2gen.app.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateToAuth: () -> Unit,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val apiKeyLast4 by viewModel.apiKeyLast4.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("API Key 초기화") },
            text = { Text("저장된 OpenAI API Key를 기기에서 삭제하고 첫 화면으로 돌아갑니다. 계속하시겠습니까?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearApiKey(onCleared = onNavigateToAuth)
                        showLogoutDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("초기화")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("취소")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("설정 및 정보") },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // ── 보안 정보 섹션 ──
            SettingsSection(title = "보안 및 계정") {
                SettingsCard(
                    icon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    title = "API Key 보안",
                    description = "입력하신 API Key는 기기 내의 안전한 암호화소(Android Keystore)에만 저장되며, 어떠한 외부 서버로도 전송되지 않습니다."
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("현재 등록된 키", fontWeight = FontWeight.Bold)
                            Text(apiKeyLast4 ?: "키 없음", style = MaterialTheme.typography.bodyMedium)
                        }
                        Button(
                            onClick = { showLogoutDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("키 삭제")
                        }
                    }
                }
            }

            // ── 정책 섹션 ──
            SettingsSection(title = "이용 정책") {
                SettingsCard(
                    icon = { Icon(Icons.Filled.Policy, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    title = "AI 콘텐츠 정책 (UGC)",
                    description = "본 앱은 불법, 혐오, 성인물 등 부적절한 이미지의 생성을 금지합니다. 생성된 모든 콘텐츠의 책임은 사용자에게 있으며, OpenAI의 콘텐츠 정책에 위배되는 프롬프트는 자동으로 거절됩니다."
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://labs.openai.com/policies/content-policy"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("OpenAI 콘텐츠 정책 보기")
                }
            }

            // ── 앱 정보 ──
            SettingsSection(title = "앱 정보") {
                SettingsCard(
                    icon = { Icon(Icons.Filled.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    title = "ima2-gen Android",
                    description = "버전 0.1.0\n디자인 및 개발 진행 중"
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        content()
    }
}

@Composable
private fun SettingsCard(icon: @Composable () -> Unit, title: String, description: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            icon()
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = description, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
