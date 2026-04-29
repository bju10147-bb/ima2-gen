package com.ima2gen.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ima2gen.app.data.local.db.PromptPresetEntity

@Composable
fun PromptPresetSelector(
    presets: List<PromptPresetEntity>,
    selectedPresetId: String?,
    onPresetSelected: (PromptPresetEntity?) -> Unit,
    onCreatePreset: (String, String) -> Unit,
    onDeletePreset: (String) -> Unit,
    currentPrompt: String
) {
    var expanded by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var newPresetName by remember { mutableStateOf("") }
    
    val selectedPreset = presets.find { it.id == selectedPresetId }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("현재 프롬프트를 프리셋으로 저장") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newPresetName,
                        onValueChange = { newPresetName = it },
                        label = { Text("프리셋 이름") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("내용: ${if(currentPrompt.length > 50) currentPrompt.take(50) + "..." else currentPrompt}", style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPresetName.isNotBlank() && currentPrompt.isNotBlank()) {
                            onCreatePreset(newPresetName, currentPrompt)
                            newPresetName = ""
                            showAddDialog = false
                        }
                    }
                ) { Text("저장") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("취소") }
            }
        )
    }

    Surface(
        onClick = { expanded = true },
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
        modifier = Modifier.wrapContentWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Filled.AutoFixHigh, 
                contentDescription = null, 
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.tertiary
            )
            Text(
                text = selectedPreset?.name ?: "기본", 
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Icon(
                Icons.Filled.ArrowDropDown, 
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            DropdownMenuItem(
                text = { Text("기본 (프리셋 없음)") },
                onClick = {
                    onPresetSelected(null)
                    expanded = false
                },
                leadingIcon = { Icon(Icons.Filled.RestartAlt, contentDescription = null) }
            )
            
            Divider()

            presets.forEach { preset ->
                DropdownMenuItem(
                    text = { 
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                preset.name, 
                                modifier = Modifier.weight(1f),
                                fontWeight = if (preset.id == selectedPresetId) FontWeight.Bold else FontWeight.Normal
                            )
                            IconButton(
                                onClick = { onDeletePreset(preset.id) },
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
                    },
                    onClick = {
                        onPresetSelected(preset)
                        expanded = false
                    }
                )
            }
            Divider()
            DropdownMenuItem(
                text = { Text("현재 프롬프트 저장...") },
                leadingIcon = { Icon(Icons.Filled.Save, contentDescription = null) },
                onClick = {
                    showAddDialog = true
                    expanded = false
                },
                enabled = currentPrompt.isNotBlank()
            )
        }
    }
}
