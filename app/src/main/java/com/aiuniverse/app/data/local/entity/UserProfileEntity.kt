package com.aiuniverse.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aiuniverse.app.domain.model.UserProfile

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val avatarUri: String?,
    val customSystemPrompt: String,
    val totalTokensUsed: Long,
    val favoriteModelId: String
) {
    fun toDomain(): UserProfile = UserProfile(
        id = id,
        name = name,
        email = email,
        avatarUri = avatarUri,
        customSystemPrompt = customSystemPrompt,
        totalTokensUsed = totalTokensUsed,
        favoriteModelId = favoriteModelId
    )

    companion object {
        fun fromDomain(p: UserProfile): UserProfileEntity = UserProfileEntity(
            id = p.id,
            name = p.name,
            email = p.email,
            avatarUri = p.avatarUri,
            customSystemPrompt = p.customSystemPrompt,
            totalTokensUsed = p.totalTokensUsed,
            favoriteModelId = p.favoriteModelId
        )
    }
}
