package com.aiuniverse.app.di

import com.aiuniverse.app.data.repository.AIProviderRepositoryImpl
import com.aiuniverse.app.data.repository.ChatRepositoryImpl
import com.aiuniverse.app.data.repository.ImageGenerationRepositoryImpl
import com.aiuniverse.app.data.repository.SettingsRepositoryImpl
import com.aiuniverse.app.domain.repository.AIProviderRepository
import com.aiuniverse.app.domain.repository.ChatRepository
import com.aiuniverse.app.domain.repository.ImageGenerationRepository
import com.aiuniverse.app.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindChatRepository(
        impl: ChatRepositoryImpl
    ): ChatRepository

    @Binds
    @Singleton
    abstract fun bindAIProviderRepository(
        impl: AIProviderRepositoryImpl
    ): AIProviderRepository

    @Binds
    @Singleton
    abstract fun bindImageGenerationRepository(
        impl: ImageGenerationRepositoryImpl
    ): ImageGenerationRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        impl: SettingsRepositoryImpl
    ): SettingsRepository
}
