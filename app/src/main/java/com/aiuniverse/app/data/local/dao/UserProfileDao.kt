package com.aiuniverse.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aiuniverse.app.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {

    @Query("SELECT * FROM user_profiles WHERE id = :userId LIMIT 1")
    fun getUserProfile(userId: String = "user_default"): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profiles WHERE id = :userId LIMIT 1")
    suspend fun getUserProfileOnce(userId: String = "user_default"): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfileEntity)

    @Update
    suspend fun updateProfile(profile: UserProfileEntity)

    @Query("UPDATE user_profiles SET totalTokensUsed = totalTokensUsed + :tokens WHERE id = :userId")
    suspend fun addTokenUsage(tokens: Long, userId: String = "user_default")
}
