package com.openai.api.aichat.common.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.openai.api.aichat.common.model.Chat
import com.openai.api.aichat.common.model.Message
import java.util.concurrent.CopyOnWriteArrayList

@Dao
interface ChatDao {
    @Query("SELECT * FROM Chat ORDER BY cid DESC LIMIT 100")
    fun getAll(): List<Chat>

    @Query("SELECT * FROM Chat ORDER BY timestamp DESC LIMIT 1")
    fun getLast(): Chat?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(chat: Chat)

    @Query("DELETE FROM Chat WHERE cid = :cid")
    fun delete(cid: String)
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM Message WHERE cid = :cid ORDER BY timestamp ASC LIMIT 200")
    fun getMessagesForChat(cid: String): List<Message>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(message: Message)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(messages: CopyOnWriteArrayList<Message>)

    @Query("DELETE FROM Message WHERE mid = :mid")
    fun delete(mid: String)
}
