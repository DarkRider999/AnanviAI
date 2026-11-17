package com.aiuniverse.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aiuniverse.app.data.local.dao.AIProviderDao
import com.aiuniverse.app.data.local.dao.AppSettingsDao
import com.aiuniverse.app.data.local.dao.ChatSessionDao
import com.aiuniverse.app.data.local.dao.MessageDao
import com.aiuniverse.app.data.local.dao.UserProfileDao
import com.aiuniverse.app.data.local.entity.AIProviderEntity
import com.aiuniverse.app.data.local.entity.AppSettingsEntity
import com.aiuniverse.app.data.local.entity.ChatSessionEntity
import com.aiuniverse.app.data.local.entity.MessageEntity
import com.aiuniverse.app.data.local.entity.UserProfileEntity

@Database(
    entities = [
        ChatSessionEntity::class,
        MessageEntity::class,
        UserProfileEntity::class,
        AIProviderEntity::class,
        AppSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatSessionDao(): ChatSessionDao
    abstract fun messageDao(): MessageDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun aiProviderDao(): AIProviderDao
    abstract fun appSettingsDao(): AppSettingsDao
}
