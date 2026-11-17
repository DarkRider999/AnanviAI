package com.aiuniverse.app.di

import android.content.Context
import androidx.room.Room
import com.aiuniverse.app.common.Constants
import com.aiuniverse.app.data.local.AppDatabase
import com.aiuniverse.app.data.local.dao.AIProviderDao
import com.aiuniverse.app.data.local.dao.AppSettingsDao
import com.aiuniverse.app.data.local.dao.ChatSessionDao
import com.aiuniverse.app.data.local.dao.MessageDao
import com.aiuniverse.app.data.local.dao.UserProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            Constants.DATABASE_NAME
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideChatSessionDao(database: AppDatabase): ChatSessionDao = database.chatSessionDao()

    @Provides
    fun provideMessageDao(database: AppDatabase): MessageDao = database.messageDao()

    @Provides
    fun provideUserProfileDao(database: AppDatabase): UserProfileDao = database.userProfileDao()

    @Provides
    fun provideAIProviderDao(database: AppDatabase): AIProviderDao = database.aiProviderDao()

    @Provides
    fun provideAppSettingsDao(database: AppDatabase): AppSettingsDao = database.appSettingsDao()
}
