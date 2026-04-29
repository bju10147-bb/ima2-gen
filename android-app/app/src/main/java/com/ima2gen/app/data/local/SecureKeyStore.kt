package com.ima2gen.app.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Securely stores sensitive data (API key) using EncryptedSharedPreferences
 * backed by Android Keystore (hardware-level encryption on supported devices).
 *
 * The master key is AES-256-GCM and stored in the Android Keystore system.
 * Values are encrypted with AES-256-GCM, keys with AES-256-SIV.
 */
@Singleton
class SecureKeyStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    companion object {
        private const val PREFS_NAME = "ima2_secure_prefs"
        private const val KEY_API_KEY = "openai_api_key"
        private const val KEY_SERVER_URL = "server_url"
        private const val DEFAULT_SERVER_URL = "http://192.168.0.1:3333"
    }

    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val prefs: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    // ── API Key ──

    fun saveApiKey(key: String) {
        prefs.edit().putString(KEY_API_KEY, key).apply()
    }

    fun getApiKey(): String? = prefs.getString(KEY_API_KEY, null)

    fun clearApiKey() {
        prefs.edit().remove(KEY_API_KEY).apply()
    }

    fun hasApiKey(): Boolean = !getApiKey().isNullOrBlank()

    /**
     * Quick format check — the key should start with "sk-" and be at least
     * 20 characters long. This does NOT validate against the OpenAI API.
     */
    fun isValidKeyFormat(key: String): Boolean {
        return key.startsWith("sk-") && key.length >= 20
    }

    // ── Server URL ──

    fun saveServerUrl(url: String) {
        prefs.edit().putString(KEY_SERVER_URL, url.trimEnd('/')).apply()
    }

    fun getServerUrl(): String {
        return prefs.getString(KEY_SERVER_URL, DEFAULT_SERVER_URL) ?: DEFAULT_SERVER_URL
    }

    fun hasServerUrl(): Boolean {
        return prefs.getString(KEY_SERVER_URL, null) != null
    }
}
