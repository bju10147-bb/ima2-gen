package com.ima2gen.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ima2gen.app.data.local.db.SessionEntity

@Composable
fun SessionSelector(
    sessions: List<SessionEntity>,
    selectedSessionId: String?,
    onSessionSelected: (String) -> Unit,
    onCreateSession: (String) -> Unit,
    onDeleteSession: (String) -> Unit,
    onRenameSession: (String, String) -> Unit = { _, _ -> },
    isCompact: Boolean = false // New parameter
) {
    var expanded by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<SessionEntity?>(null) }
    var newSessionName by remember { mutableStateOf("") }
    var editSessionName by remember { mutableStateOf("") }
    
    val selectedSession = sessions.find { it.id == selectedSessionId }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("새 세션 생성") },
            text = {
                OutlinedTextField(
                    value = newSessionName,
                    onValueChange = { newSessionName = it },
                    label = { Text("세션 이름") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newSessionName.isNotBlank()) {
                            onCreateSession(newSessionName)
                            newSessionName = ""
                            showAddDialog = false
                        }
                    }
                ) { Text("생성") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("취소") }
            }
        )
    }

    if (showEditDialog != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = null },
            title = { Text("세션 이름 변경") },
            text = {
                OutlinedTextField(
                    value = editSessionName,
                    onValueChange = { editSessionName = it },
                    label = { Text("새 이름") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editSessionName.isNotBlank()) {
                            onRenameSession(showEditDialog!!.id, editSessionName)
                            showEditDialog = null
                        }
                    }
                ) { Text("변경") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = null }) { Text("취소") }
            }
        )
    }

    Box(modifier = if (isCompact) Modifier.fillMaxWidth() else Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
        Surface(
            onClick = { expanded = true },
            shape = MaterialTheme.shapes.small,
            color = if (isCompact) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
            modifier = if (isCompact) Modifier.fillMaxWidth() else Modifier.wrapContentWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = if (isCompact) 4.dp else 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    Icons.Filled.DynamicFeed, 
                    contentDescription = null, 
                    modifier = Modifier.size(if (isCompact) 16.dp else 20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = selectedSession?.name ?: "세션", 
                    style = if (isCompact) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isCompact) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSecondaryContainer,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    Icons.Filled.ArrowDropDown, 
                    contentDescription = null,
                    modifier = Modifier.size(if (isCompact) 16.dp else 20.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            sessions.forEach { session ->
                DropdownMenuItem(
                    text = { 
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                session.name, 
                                modifier = Modifier.weight(1f),
                                fontWeight = if (session.id == selectedSessionId) FontWeight.Bold else FontWeight.Normal
                            )
                            Row {
                                IconButton(
                                    onClick = { 
                                        editSessionName = session.name
                                        showEditDialog = session 
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.Edit, 
                                        contentDescription = "Edit", 
                                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { onDeleteSession(session.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.Delete, 
                                        contentDescription = "Delete", 
                                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    },
                    onClick = {
                        onSessionSelected(session.id)
                        expanded = false
                    }
                )
            }
            Divider()
            DropdownMenuItem(
                text = { Text("새 세션 추가...") },
                leadingIcon = { Icon(Icons.Filled.Add, contentDescription = null) },
                onClick = {
                    showAddDialog = true
                    expanded = false
                }
            )
        }
    }
}
