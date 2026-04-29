package com.ima2gen.app.ui.auth

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiKeyGuideScreen(
    onBack: () -> Unit,
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("API Key 발급 가이드") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
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
            GuideStep(
                step = 1,
                title = "OpenAI 접속 및 로그인",
                description = "아래 버튼을 눌러 OpenAI API 페이지로 이동한 후 로그인을 진행합니다."
            )

            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://platform.openai.com/api-keys"))
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("OpenAI API Keys 페이지 열기")
            }

            GuideStep(
                step = 2,
                title = "새로운 키 생성",
                description = "화면 중앙의 'Create new secret key' 버튼을 누릅니다. 이름은 임의로 지정하셔도 됩니다."
            )

            GuideStep(
                step = 3,
                title = "키 복사",
                description = "생성된 'sk-'로 시작하는 키 옆의 복사 아이콘을 눌러 키를 복사합니다. 이 창을 닫으면 다시 볼 수 없으므로 반드시 복사해야 합니다."
            )

            GuideStep(
                step = 4,
                title = "앱에 붙여넣기",
                description = "이 앱으로 돌아오면 복사된 키를 감지하여 자동으로 입력을 도와줍니다. 또는 직접 붙여넣기를 해주세요."
            )
            
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "주의사항",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• API Key는 절대 타인에게 공유하지 마세요.\n• OpenAI API는 유료 서비스이므로, 원활한 사용을 위해 OpenAI 계정에 결제 수단(Credit)이 등록되어 있어야 합니다.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun GuideStep(step: Int, title: String, description: String) {
    Row(verticalAlignment = Alignment.Top) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = step.toString(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
