package com.aiuniverse.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aiuniverse.app.domain.model.AppSettings

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val themeMode: String,
    val streamResponses: Boolean,
    val hapticFeedback: Boolean,
    val soundEffects: Boolean,
    val defaultProviderId: String,
    val defaultModelId: String,
    val temperature: Float,
    val topP: Float
) {
    fun toDomain(): AppSettings = AppSettings(
        themeMode = themeMode,
        streamResponses = streamResponses,
        hapticFeedback = hapticFeedback,
        soundEffects = soundEffects,
        defaultProviderId = defaultProviderId,
        defaultModelId = defaultModelId,
        temperature = temperature,
        topP = topP
    )

    companion object {
        fun fromDomain(settings: AppSettings): AppSettingsEntity = AppSettingsEntity(
            id = 1,
            themeMode = settings.themeMode,
            streamResponses = settings.streamResponses,
            hapticFeedback = settings.hapticFeedback,
            soundEffects = settings.soundEffects,
            defaultProviderId = settings.defaultProviderId,
            defaultModelId = settings.defaultModelId,
            temperature = settings.temperature,
            topP = settings.topP
        )
    }
}
