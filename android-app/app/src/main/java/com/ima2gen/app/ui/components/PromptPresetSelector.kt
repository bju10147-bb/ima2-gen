package com.ima2gen.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    var newPresetContent by remember { mutableStateOf("") }
    
    val selectedPreset = presets.find { it.id == selectedPresetId }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("스타일 프리셋 저장") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "프리셋은 프롬프트에 자동으로 적용되는 스타일/지시 사항입니다.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = newPresetName,
                        onValueChange = { newPresetName = it },
                        label = { Text("프리셋 이름") },
                        placeholder = { Text("예: 실사풍, 애니 스타일...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newPresetContent,
                        onValueChange = { newPresetContent = it },
                        label = { Text("프리셋 내용") },
                        placeholder = { Text("예: 실사 사진처럼, 자연광, 높은 디테일...") },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp),
                        maxLines = 5
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPresetName.isNotBlank() && newPresetContent.isNotBlank()) {
                            onCreatePreset(newPresetName, newPresetContent)
                            newPresetName = ""
                            newPresetContent = ""
                            showAddDialog = false
                        }
                    },
                    enabled = newPresetName.isNotBlank() && newPresetContent.isNotBlank()
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
        color = if (selectedPreset != null)
            MaterialTheme.colorScheme.secondaryContainer
        else
            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
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
                tint = if (selectedPreset != null) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary
            )
            Text(
                text = selectedPreset?.name ?: "프리셋", 
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (selectedPreset != null) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onTertiaryContainer
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
                text = { Text("프리셋 없음") },
                onClick = {
                    onPresetSelected(null)
                    expanded = false
                },
                leadingIcon = { Icon(Icons.Filled.RestartAlt, contentDescription = null) },
                trailingIcon = {
                    if (selectedPresetId == null) {
                        Icon(Icons.Filled.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    }
                }
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    preset.name, 
                                    fontWeight = if (preset.id == selectedPresetId) FontWeight.Bold else FontWeight.Normal,
                                    color = if (preset.id == selectedPresetId) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    preset.content.let { if (it.length > 40) it.take(40) + "..." else it },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    maxLines = 1
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
                                if (preset.id == selectedPresetId) {
                                    Icon(
                                        Icons.Filled.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
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
                text = { Text("새 프리셋 만들기...") },
                leadingIcon = { Icon(Icons.Filled.Add, contentDescription = null) },
                onClick = {
                    showAddDialog = true
                    expanded = false
                }
            )
        }
    }
}
