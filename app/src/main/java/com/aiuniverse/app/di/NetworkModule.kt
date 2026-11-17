package com.aiuniverse.app.di

import com.aiuniverse.app.common.Constants
import com.aiuniverse.app.data.remote.api.GroqApi
import com.aiuniverse.app.data.remote.api.HuggingFaceApi
import com.aiuniverse.app.data.remote.api.HuggingFaceChatApi
import com.aiuniverse.app.data.remote.api.OllamaApi
import com.aiuniverse.app.data.remote.api.OpenAIApi
import com.aiuniverse.app.data.remote.api.OpenRouterApi
import com.aiuniverse.app.data.remote.api.TogetherApi
import com.aiuniverse.app.data.remote.interceptor.AuthHeaderInterceptor
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthHeaderInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideHuggingFaceApi(okHttpClient: OkHttpClient, gson: Gson): HuggingFaceApi {
        // Legacy model-task API remains isolated for image-generation calls.
        return Retrofit.Builder()
            .baseUrl(Constants.HUGGING_FACE_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(HuggingFaceApi::class.java)
    }

    @Provides
    @Singleton
    fun provideHuggingFaceChatApi(okHttpClient: OkHttpClient, gson: Gson): HuggingFaceChatApi {
        return Retrofit.Builder()
            .baseUrl(Constants.HUGGING_FACE_ROUTER_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(HuggingFaceChatApi::class.java)
    }

    @Provides
    @Singleton
    fun provideOpenRouterApi(okHttpClient: OkHttpClient, gson: Gson): OpenRouterApi {
        return Retrofit.Builder()
            .baseUrl(Constants.OPENROUTER_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(OpenRouterApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGroqApi(okHttpClient: OkHttpClient, gson: Gson): GroqApi {
        return Retrofit.Builder()
            .baseUrl(Constants.GROQ_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(GroqApi::class.java)
    }

    @Provides
    @Singleton
    fun provideOpenAIApi(okHttpClient: OkHttpClient, gson: Gson): OpenAIApi {
        return Retrofit.Builder()
            .baseUrl(Constants.OPENAI_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(OpenAIApi::class.java)
    }

    @Provides
    @Singleton
    fun provideOllamaApi(okHttpClient: OkHttpClient, gson: Gson): OllamaApi {
        return Retrofit.Builder()
            .baseUrl(Constants.OLLAMA_DEFAULT_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(OllamaApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTogetherApi(okHttpClient: OkHttpClient, gson: Gson): TogetherApi {
        return Retrofit.Builder()
            .baseUrl(Constants.TOGETHER_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(TogetherApi::class.java)
    }
}
