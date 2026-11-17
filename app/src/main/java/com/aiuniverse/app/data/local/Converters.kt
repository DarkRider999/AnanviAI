package com.aiuniverse.app.data.local

import androidx.room.TypeConverter
import com.aiuniverse.app.domain.model.MediaType
import com.aiuniverse.app.domain.model.MessageRole

class Converters {
    @TypeConverter
    fun fromMessageRole(role: MessageRole): String = role.name

    @TypeConverter
    fun toMessageRole(value: String): MessageRole = try {
        MessageRole.valueOf(value)
    } catch (e: Exception) {
        MessageRole.USER
    }

    @TypeConverter
    fun fromMediaType(type: MediaType): String = type.name

    @TypeConverter
    fun toMediaType(value: String): MediaType = try {
        MediaType.valueOf(value)
    } catch (e: Exception) {
        MediaType.NONE
    }
}
