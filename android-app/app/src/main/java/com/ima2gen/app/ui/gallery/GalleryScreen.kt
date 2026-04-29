package com.ima2gen.app.ui.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
    var isPromptExpanded by remember { mutableStateOf(false) }
    var showFullScreen by remember { mutableStateOf(false) }

    // ── Full Screen Zoom Dialog ──
    if (showFullScreen && selectedItem != null) {
        FullScreenImageDialog(
            imageUrl = selectedItem!!.imageUrl,
            onDismiss = { showFullScreen = false }
        )
    }

    if (selectedItem != null) {
        AlertDialog(
            onDismissRequest = { 
                selectedItem = null
                isPromptExpanded = false
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.clickable { showFullScreen = true }
                    ) {
                        coil.compose.AsyncImage(
                            model = selectedItem!!.imageUrl,
                            contentDescription = "Detail Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium),
                            contentScale = ContentScale.FillWidth
                        )
                    }
                    
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isPromptExpanded = !isPromptExpanded }
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = if (isPromptExpanded) "프롬프트 (전체):" else "프롬프트 (클릭하여 보기):",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = selectedItem!!.prompt,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = if (isPromptExpanded) Int.MAX_VALUE else 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }
                    }

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

@Composable
fun FullScreenImageDialog(imageUrl: String, onDismiss: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        var scale by remember { mutableStateOf(1f) }
        var offset by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f))
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
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    ),
                contentScale = ContentScale.Fit
            )
            
            // Top Controls
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SessionGallerySection(group: SessionGroup, onItemClick: (HistoryEntity) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Filled.Folder, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Text(text = group.session.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = "(${group.items.size}장)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val spacing = 8.dp
            val itemWidth = (maxWidth - (spacing * 2)) / 3
            FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spacing), verticalArrangement = Arrangement.spacedBy(spacing), maxItemsInEachRow = 3) {
                group.items.forEach { item ->
                    Card(modifier = Modifier.width(itemWidth).aspectRatio(1f).clickable { onItemClick(item) }, elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                        coil.compose.AsyncImage(model = item.imageUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    }
                }
            }
        }
    }
}
