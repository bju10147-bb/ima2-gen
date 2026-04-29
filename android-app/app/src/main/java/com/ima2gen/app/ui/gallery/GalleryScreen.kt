package com.ima2gen.app.ui.gallery

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.ima2gen.app.data.local.db.HistoryEntity
import com.ima2gen.app.ui.components.SessionSelector
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val historyItems by viewModel.historyItems.collectAsState()
    val sessions by viewModel.sessions.collectAsState()
    val selectedSessionId by viewModel.selectedSessionId.collectAsState()
    val filterMode by viewModel.filterMode.collectAsState()
    var selectedItem by remember { mutableStateOf<HistoryEntity?>(null) }

    if (selectedItem != null) {
        AlertDialog(
            onDismissRequest = { selectedItem = null },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    coil.compose.AsyncImage(
                        model = selectedItem!!.imageUrl,
                        contentDescription = "Detail Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.FillWidth
                    )
                    Text("프롬프트:", style = MaterialTheme.typography.titleSmall)
                    Text(selectedItem!!.prompt, style = MaterialTheme.typography.bodyMedium)
                }
            },
            confirmButton = {
                val context = androidx.compose.ui.platform.LocalContext.current
                val scope = androidx.compose.runtime.rememberCoroutineScope()
                Row {
                    IconButton(onClick = { 
                        scope.launch { com.ima2gen.app.util.ImageActionHelper.shareImage(context, selectedItem!!.imageUrl) }
                    }) {
                        Icon(Icons.Filled.Share, contentDescription = "Share")
                    }
                    IconButton(onClick = { 
                        scope.launch { com.ima2gen.app.util.ImageActionHelper.downloadImage(context, selectedItem!!.imageUrl) }
                    }) {
                        Icon(Icons.Filled.Download, contentDescription = "Download")
                    }
                    TextButton(onClick = { selectedItem = null }) {
                        Text("닫기")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteHistory(selectedItem!!.id)
                        selectedItem = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("삭제")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("갤러리 (히스토리)") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // ── Filter & Session Row ──
            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = filterMode == GalleryFilterMode.ALL,
                        onClick = { viewModel.setFilterMode(GalleryFilterMode.ALL) },
                        label = { Text("전체 보기") },
                        leadingIcon = if (filterMode == GalleryFilterMode.ALL) {
                            { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        } else null
                    )
                    FilterChip(
                        selected = filterMode == GalleryFilterMode.SESSION,
                        onClick = { viewModel.setFilterMode(GalleryFilterMode.SESSION) },
                        label = { Text("세션별 보기") },
                        leadingIcon = if (filterMode == GalleryFilterMode.SESSION) {
                            { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        } else null
                    )
                }

                // Show SessionSelector only when in SESSION mode
                if (filterMode == GalleryFilterMode.SESSION) {
                    SessionSelector(
                        sessions = sessions,
                        selectedSessionId = selectedSessionId,
                        onSessionSelected = viewModel::selectSession,
                        onCreateSession = viewModel::createSession,
                        onDeleteSession = viewModel::deleteSession
                    )
                } else {
                    // Spacer or project info
                    Text(
                        "현재 프로젝트의 모든 이미지를 표시합니다.",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }
            }

            if (historyItems.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (filterMode == GalleryFilterMode.SESSION) "이 세션에 생성된 이미지가 없습니다." else "프로젝트에 생성된 이미지가 없습니다.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    modifier = Modifier.weight(1f).fillMaxWidth()
                ) {
                    items(historyItems, key = { it.id }) { item ->
                        GalleryItemCard(
                            item = item,
                            onClick = { selectedItem = item }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GalleryItemCard(item: HistoryEntity, onClick: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("MM/dd HH:mm", Locale.getDefault()) }
    
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            coil.compose.AsyncImage(
                model = item.imageUrl,
                contentDescription = item.prompt,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentScale = ContentScale.Crop
            )
            Text(
                text = item.prompt,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = dateFormat.format(Date(item.createdAt)),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )
        }
    }
}
