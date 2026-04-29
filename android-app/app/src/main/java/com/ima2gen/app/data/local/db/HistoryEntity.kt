package com.ima2gen.app.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_items")
data class HistoryEntity(
    @PrimaryKey val id: String, // UUID
    val prompt: String,
    val revisedPrompt: String?,
    val imageUrl: String, // Web URL or local File path
    val createdAt: Long = System.currentTimeMillis()
)
