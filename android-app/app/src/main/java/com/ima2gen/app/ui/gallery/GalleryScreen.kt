package com.ima2gen.app.ui.gallery

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
    val sessionGroups by viewModel.sessionGroups.collectAsState()
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
                    Text(
                        "생성일: ${SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(Date(selectedItem!!.createdAt))}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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

                if (filterMode == GalleryFilterMode.SESSION) {
                    SessionSelector(
                        sessions = sessions,
                        selectedSessionId = selectedSessionId,
                        onSessionSelected = viewModel::selectSession,
                        onCreateSession = viewModel::createSession,
                        onDeleteSession = viewModel::deleteSession
                    )
                }
            }

            if (sessionGroups.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (filterMode == GalleryFilterMode.SESSION) "이 세션에 이미지가 없습니다." else "프로젝트에 이미지가 없습니다.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    items(sessionGroups) { group ->
                        SessionGallerySection(
                            group = group,
                            onItemClick = { selectedItem = it }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SessionGallerySection(group: SessionGroup, onItemClick: (HistoryEntity) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Filled.Folder, 
                contentDescription = null, 
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = group.session.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "(${group.items.size}장)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Use FlowRow to display images in 3 columns
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val spacing = 8.dp
            val itemWidth = (maxWidth - (spacing * 2)) / 3
            
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing),
                verticalArrangement = Arrangement.spacedBy(spacing),
                maxItemsInEachRow = 3
            ) {
                group.items.forEach { item ->
                    Card(
                        modifier = Modifier
                            .width(itemWidth)
                            .aspectRatio(1f)
                            .clickable { onItemClick(item) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
