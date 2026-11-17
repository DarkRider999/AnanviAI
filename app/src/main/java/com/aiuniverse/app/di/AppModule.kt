package com.aiuniverse.app.di

import com.aiuniverse.app.common.DefaultDispatcherProvider
import com.aiuniverse.app.common.DispatcherProvider
import com.aiuniverse.app.data.remote.sse.SseStreamParser
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .setLenient()
        .create()

    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider = DefaultDispatcherProvider()

    @Provides
    @Singleton
    fun provideSseStreamParser(gson: Gson): SseStreamParser = SseStreamParser(gson)
}
