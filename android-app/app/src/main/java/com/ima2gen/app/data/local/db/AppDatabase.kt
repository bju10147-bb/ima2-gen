package com.ima2gen.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ProjectEntity::class, SessionEntity::class, HistoryEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}
