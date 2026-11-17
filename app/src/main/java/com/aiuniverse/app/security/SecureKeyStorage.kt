package com.aiuniverse.app.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.aiuniverse.app.common.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Stores provider credentials only in EncryptedSharedPreferences backed by Android Keystore.
 *
 * Existing credentials are migrated from the previous store on first access. The legacy
 * file is deleted only after the encrypted destination has committed successfully.
 * There is deliberately no plaintext fallback: if Keystore is unavailable, key operations
 * fail closed instead of silently writing secrets in ordinary SharedPreferences.
 */
@Singleton
class SecureKeyStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val preferences: SharedPreferences by lazy {
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val encrypted = EncryptedSharedPreferences.create(
                context,
                Constants.ENCRYPTED_PREFS_NAME_V2,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            migrateLegacyKeys(masterKey, encrypted)
            encrypted
        } catch (e: Exception) {
            throw IllegalStateException(
                "Secure API-key storage is unavailable. Credentials were not written to plaintext storage.",
                e
            )
        }
    }

    private fun migrateLegacyKeys(masterKey: MasterKey, destination: SharedPreferences) {
        val legacyName = Constants.ENCRYPTED_PREFS_NAME

        // First try the previous EncryptedSharedPreferences format.
        val oldEncrypted = try {
            EncryptedSharedPreferences.create(
                context,
                legacyName,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (_: Exception) {
            null
        }

        val encryptedEntries = try {
            oldEncrypted?.all
        } catch (_: Exception) {
            null
        }

        if (!encryptedEntries.isNullOrEmpty()) {
            val editor = destination.edit()
            encryptedEntries.forEach { (key, value) ->
                when (value) {
                    is String -> editor.putString(key, value)
                    is Boolean -> editor.putBoolean(key, value)
                    is Int -> editor.putInt(key, value)
                    is Long -> editor.putLong(key, value)
                    is Float -> editor.putFloat(key, value)
                    else -> Unit
                }
            }
            if (!editor.commit()) {
                throw IllegalStateException("Could not migrate existing encrypted API keys.")
            }
            context.deleteSharedPreferences(legacyName)
            return
        }

        // Older app builds had a plaintext fallback. Migrate only clearly named API-key
        // entries; do not copy encrypted/opaque values from an unreadable legacy file.
        val legacyPlain = context.getSharedPreferences(legacyName, Context.MODE_PRIVATE)
        val plainEntries = legacyPlain.all
            .filterKeys { it.startsWith(KEY_PREFIX) }
            .mapNotNull { (key, value) ->
                (value as? String)?.let { key to it }
            }

        if (plainEntries.isNotEmpty()) {
            val editor = destination.edit()
            plainEntries.forEach { (key, value) -> editor.putString(key, value) }
            if (!editor.commit()) {
                throw IllegalStateException("Could not securely migrate legacy API keys.")
            }
            val cleanup = legacyPlain.edit()
            plainEntries.forEach { (key, _) -> cleanup.remove(key) }
            if (!cleanup.commit()) {
                // The encrypted copy is already safe; leave the legacy file for manual cleanup.
                return
            }
        }
    }

    fun saveApiKey(providerId: String, apiKey: String) {
        preferences.edit().putString(KEY_PREFIX + providerId, apiKey.trim()).apply()
    }

    fun getApiKey(providerId: String): String {
        return preferences.getString(KEY_PREFIX + providerId, "") ?: ""
    }

    fun removeApiKey(providerId: String) {
        preferences.edit().remove(KEY_PREFIX + providerId).apply()
    }

    fun hasApiKey(providerId: String): Boolean {
        return getApiKey(providerId).isNotBlank()
    }

    fun clearAllKeys() {
        preferences.edit().clear().apply()
    }

    companion object {
        private const val KEY_PREFIX = "api_key_"
    }
}
