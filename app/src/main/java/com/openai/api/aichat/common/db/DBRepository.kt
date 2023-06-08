package com.openai.api.aichat.common.db

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.openai.api.aichat.common.model.Chat
import com.openai.api.aichat.common.model.Message
import java.util.concurrent.CopyOnWriteArrayList

class DBRepository(private val chatDao: ChatDao, private val messageDao: MessageDao) {

    suspend fun insertChat(chat: Chat) = withContext(Dispatchers.IO) {
        chatDao.insert(chat)
    }

    suspend fun getAllChats(): List<Chat> = withContext(Dispatchers.IO) {
        chatDao.getAll()
    }

    suspend fun getLastChat(): Chat? = withContext(Dispatchers.IO) {
        chatDao.getLast()
    }

    suspend fun deleteChat(cid: String) = withContext(Dispatchers.IO) {
        chatDao.delete(cid)
    }

    suspend fun insertMessage(message: Message) = withContext(Dispatchers.IO) {
        messageDao.insert(message)
    }

    suspend fun insertMessages(messages: CopyOnWriteArrayList<Message>) = withContext(Dispatchers.IO) {
        messageDao.insertAll(messages)
    }

    suspend fun getMessagesForChat(cid: String): List<Message> = withContext(Dispatchers.IO) {
        messageDao.getMessagesForChat(cid)
    }

    suspend fun deleteMessage(mid: String) = withContext(Dispatchers.IO) {
        messageDao.delete(mid)
    }
}

