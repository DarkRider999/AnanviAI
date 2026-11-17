package com.aiuniverse.app.domain.repository

import com.aiuniverse.app.domain.model.AppSettings
import com.aiuniverse.app.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<AppSettings>
    suspend fun updateSettings(settings: AppSettings)
    fun getUserProfile(): Flow<UserProfile>
    suspend fun updateUserProfile(profile: UserProfile)
    suspend fun recordTokenUsage(tokens: Long)
    val isOnboardingCompleted: Flow<Boolean>
    suspend fun setOnboardingCompleted(completed: Boolean)
}
