package com.aiuniverse.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aiuniverse.app.data.local.entity.AIProviderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AIProviderDao {

    @Query("SELECT * FROM ai_providers ORDER BY name ASC")
    fun getAllProviders(): Flow<List<AIProviderEntity>>

    @Query("SELECT * FROM ai_providers WHERE id = :id LIMIT 1")
    suspend fun getProviderById(id: String): AIProviderEntity?

    @Query("SELECT * FROM ai_providers WHERE id = :id LIMIT 1")
    fun getProviderFlow(id: String): Flow<AIProviderEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProviders(providers: List<AIProviderEntity>)

    @Update
    suspend fun updateProvider(provider: AIProviderEntity)

    @Query("UPDATE ai_providers SET isConfigured = :isConfigured WHERE id = :id")
    suspend fun updateConfigurationStatus(id: String, isConfigured: Boolean)
}
