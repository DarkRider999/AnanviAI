package com.aiuniverse.app.data.repository

import com.aiuniverse.app.data.local.dao.AppSettingsDao
import com.aiuniverse.app.data.local.dao.UserProfileDao
import com.aiuniverse.app.data.local.datastore.UserPreferencesDataStore
import com.aiuniverse.app.data.local.entity.AppSettingsEntity
import com.aiuniverse.app.data.local.entity.UserProfileEntity
import com.aiuniverse.app.domain.model.AppSettings
import com.aiuniverse.app.domain.model.UserProfile
import com.aiuniverse.app.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val settingsDao: AppSettingsDao,
    private val profileDao: UserProfileDao,
    private val preferencesDataStore: UserPreferencesDataStore
) : SettingsRepository {

    override fun getSettings(): Flow<AppSettings> {
        return settingsDao.getSettingsFlow().map { entity ->
            entity?.toDomain() ?: AppSettings()
        }
    }

    override suspend fun updateSettings(settings: AppSettings) {
        settingsDao.insertOrUpdate(AppSettingsEntity.fromDomain(settings))
        preferencesDataStore.setAppTheme(settings.themeMode)
    }

    override fun getUserProfile(): Flow<UserProfile> {
        return profileDao.getUserProfile().map { entity ->
            entity?.toDomain() ?: UserProfile()
        }
    }

    override suspend fun updateUserProfile(profile: UserProfile) {
        profileDao.insertProfile(UserProfileEntity.fromDomain(profile))
    }

    override suspend fun recordTokenUsage(tokens: Long) {
        profileDao.addTokenUsage(tokens)
    }

    override val isOnboardingCompleted: Flow<Boolean> = preferencesDataStore.isOnboardingCompleted

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        preferencesDataStore.setOnboardingCompleted(completed)
    }
}
