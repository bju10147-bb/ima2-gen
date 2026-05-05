package com.ima2gen.app.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onNavigateToGuide: () -> Unit,
    onAuthComplete: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val apiKey by viewModel.apiKey.collectAsState()
    val isKeyValidFormat by viewModel.isKeyValidFormat.collectAsState()
    var isPasswordVisible by remember { mutableStateOf(false) }

    val clipboardManager = LocalClipboardManager.current
    var showClipboardDialog by remember { mutableStateOf(false) }
    var clipboardText by remember { mutableStateOf("") }

    // Check clipboard when screen resumes
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                val text = clipboardManager.getText()?.text
                if (text != null && text.startsWith("sk-") && text != apiKey) {
                    clipboardText = text
                    showClipboardDialog = true
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (showClipboardDialog) {
        AlertDialog(
            onDismissRequest = { showClipboardDialog = false },
            title = { Text("API Key 감지됨") },
            text = { Text("클립보드에 OpenAI API Key가 있습니다. 자동으로 입력하시겠습니까?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onApiKeyChanged(clipboardText)
                        showClipboardDialog = false
                    }
                ) {
                    Text("예, 입력합니다")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClipboardDialog = false }) {
                    Text("아니오")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ima2-gen 설정") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "환영합니다!",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "앱을 사용하려면 본인의 OpenAI API Key가 필요합니다.\n키는 기기 내의 안전한 보안 영역에만 저장됩니다.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = apiKey,
                onValueChange = viewModel::onApiKeyChanged,
                label = { Text("OpenAI API Key") },
                placeholder = { Text("sk-...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    TextButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Text(if (isPasswordVisible) "숨기기" else "보기")
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.saveConfiguration(onAuthComplete) },
                enabled = isKeyValidFormat,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("시작하기")
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onNavigateToGuide,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Info, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text("API Key 발급 방법 (1분 소요)")
            }
        }
    }
}
