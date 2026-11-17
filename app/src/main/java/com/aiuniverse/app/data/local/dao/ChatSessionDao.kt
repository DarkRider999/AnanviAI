package com.aiuniverse.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aiuniverse.app.data.local.entity.ChatSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatSessionDao {

    @Query("SELECT * FROM chat_sessions ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllSessions(): Flow<List<ChatSessionEntity>>

    @Query("SELECT * FROM chat_sessions WHERE id = :sessionId LIMIT 1")
    suspend fun getSessionById(sessionId: String): ChatSessionEntity?

    @Query("SELECT * FROM chat_sessions WHERE id = :sessionId LIMIT 1")
    fun getSessionFlow(sessionId: String): Flow<ChatSessionEntity?>

    @Query("SELECT * FROM chat_sessions WHERE title LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
    fun searchSessions(query: String): Flow<List<ChatSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ChatSessionEntity)

    @Update
    suspend fun updateSession(session: ChatSessionEntity)

    @Query("UPDATE chat_sessions SET updatedAt = :timestamp, messageCount = messageCount + 1 WHERE id = :sessionId")
    suspend fun touchSession(sessionId: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE chat_sessions SET isPinned = :isPinned WHERE id = :sessionId")
    suspend fun updatePinStatus(sessionId: String, isPinned: Boolean)

    @Query("DELETE FROM chat_sessions WHERE id = :sessionId")
    suspend fun deleteSessionById(sessionId: String)

    @Query("DELETE FROM chat_sessions")
    suspend fun deleteAllSessions()
}
