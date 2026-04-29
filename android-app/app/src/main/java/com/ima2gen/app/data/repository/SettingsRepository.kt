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
    private val IMAGE_MODEL = stringPreferenceKey("image_model")
    private val THEME = stringPreferenceKey("theme")
    private val LANGUAGE = stringPreferenceKey("language")

    val imageModel: Flow<String> = context.dataStore.data.map { it[IMAGE_MODEL] ?: "5.4" }
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
