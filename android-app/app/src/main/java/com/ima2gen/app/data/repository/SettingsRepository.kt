package com.ima2gen.app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

enum class AppTheme { SYSTEM, LIGHT, DARK }
enum class AppLanguage { SYSTEM, KO, EN, JA, ZH }

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val IMAGE_MODEL = stringPreferencesKey("image_model")
    private val THEME = stringPreferencesKey("theme")
    private val LANGUAGE = stringPreferencesKey("language")

    val imageModel: Flow<String> = context.dataStore.data.map {
        when (val saved = it[IMAGE_MODEL]) {
            "gpt-5.5", "gpt-5.4", "gpt-5.4-mini" -> saved
            "5.5" -> "gpt-5.5"
            "5.4" -> "gpt-5.4"
            "5.4mini", "gpt-5", "gpt-4.1" -> "gpt-5.5"
            else -> "gpt-5.5"
        }
    }
    val theme: Flow<AppTheme> = context.dataStore.data.map {
        AppTheme.valueOf(it[THEME] ?: AppTheme.SYSTEM.name)
    }
    val language: Flow<AppLanguage> = context.dataStore.data.map {
        AppLanguage.valueOf(it[LANGUAGE] ?: AppLanguage.SYSTEM.name)
    }

    suspend fun setImageModel(model: String) {
        context.dataStore.edit { it[IMAGE_MODEL] = model }
    }

    suspend fun setTheme(theme: AppTheme) {
        context.dataStore.edit { it[THEME] = theme.name }
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { it[LANGUAGE] = language.name }
    }
}
