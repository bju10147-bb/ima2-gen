package com.ima2gen.app.data.local.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    // ── Project ──
    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProjectById(id: String): ProjectEntity?

    @Query("SELECT * FROM projects ORDER BY createdAt DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProject(id: String)

    // ── Session ──
    @Query("SELECT * FROM sessions WHERE projectId = :projectId ORDER BY createdAt DESC")
    fun getSessionsForProject(projectId: String): Flow<List<SessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SessionEntity)

    @Query("DELETE FROM sessions WHERE id = :id")
    suspend fun deleteSession(id: String)

    // ── History ──
    @Query("SELECT history.* FROM history INNER JOIN sessions ON history.sessionId = sessions.id WHERE sessions.projectId = :projectId ORDER BY history.createdAt DESC")
    fun getAllHistoryForProject(projectId: String): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM history WHERE sessionId = :sessionId ORDER BY createdAt DESC")
    fun getHistoryForSession(sessionId: String): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity)

    @Query("DELETE FROM history WHERE id = :id")
    suspend fun deleteHistory(id: String)

    // ── Prompt Preset ──
    @Query("SELECT * FROM prompt_presets ORDER BY createdAt DESC")
    fun getAllPresets(): Flow<List<PromptPresetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreset(preset: PromptPresetEntity)

    @Query("DELETE FROM prompt_presets WHERE id = :id")
    suspend fun deletePreset(id: String)
}
