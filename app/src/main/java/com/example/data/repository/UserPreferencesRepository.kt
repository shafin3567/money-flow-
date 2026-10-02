package com.example.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

data class UserPreferences(
    val defaultCurrency: String = "USD",
    val isOnboardingCompleted: Boolean = false,
    val isAppLockEnabled: Boolean = false,
    val pinHash: String = "",
    val useBiometrics: Boolean = false,
    val lastUsedCategoryId: Long = -1L,
    val lastUsedAccountId: Long = -1L,
    val lastUsedType: String = "EXPENSE",
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM
)

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val DEFAULT_CURRENCY = stringPreferencesKey("default_currency")
        val IS_ONBOARDING_COMPLETED = booleanPreferencesKey("is_onboarding_completed")
        val IS_APP_LOCK_ENABLED = booleanPreferencesKey("is_app_lock_enabled")
        val PIN_HASH = stringPreferencesKey("pin_hash")
        val USE_BIOMETRICS = booleanPreferencesKey("use_biometrics")
        val LAST_USED_CATEGORY_ID = longPreferencesKey("last_used_category_id")
        val LAST_USED_ACCOUNT_ID = longPreferencesKey("last_used_account_id")
        val LAST_USED_TYPE = stringPreferencesKey("last_used_type")
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { preferences ->
        val themeModeStr = preferences[PreferencesKeys.THEME_MODE] ?: AppThemeMode.SYSTEM.name
        val themeMode = try {
            AppThemeMode.valueOf(themeModeStr)
        } catch (_: Exception) {
            AppThemeMode.SYSTEM
        }

        UserPreferences(
            defaultCurrency = preferences[PreferencesKeys.DEFAULT_CURRENCY] ?: "USD",
            isOnboardingCompleted = preferences[PreferencesKeys.IS_ONBOARDING_COMPLETED] ?: false,
            isAppLockEnabled = preferences[PreferencesKeys.IS_APP_LOCK_ENABLED] ?: false,
            pinHash = preferences[PreferencesKeys.PIN_HASH] ?: "",
            useBiometrics = preferences[PreferencesKeys.USE_BIOMETRICS] ?: false,
            lastUsedCategoryId = preferences[PreferencesKeys.LAST_USED_CATEGORY_ID] ?: -1L,
            lastUsedAccountId = preferences[PreferencesKeys.LAST_USED_ACCOUNT_ID] ?: -1L,
            lastUsedType = preferences[PreferencesKeys.LAST_USED_TYPE] ?: "EXPENSE",
            themeMode = themeMode
        )
    }

    suspend fun setDefaultCurrency(currency: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_CURRENCY] = currency
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setAppLock(enabled: Boolean, pinHash: String, useBiometrics: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_APP_LOCK_ENABLED] = enabled
            preferences[PreferencesKeys.PIN_HASH] = pinHash
            preferences[PreferencesKeys.USE_BIOMETRICS] = useBiometrics
        }
    }

    suspend fun updateLastUsed(categoryId: Long, accountId: Long, type: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_USED_CATEGORY_ID] = categoryId
            preferences[PreferencesKeys.LAST_USED_ACCOUNT_ID] = accountId
            preferences[PreferencesKeys.LAST_USED_TYPE] = type
        }
    }

    suspend fun setThemeMode(mode: AppThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode.name
        }
    }
}
