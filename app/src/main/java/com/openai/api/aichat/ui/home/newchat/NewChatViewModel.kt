package com.openai.api.aichat.ui.home.newchat

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.image.ImageCreation
import com.aallam.openai.api.image.ImageSize
import com.aallam.openai.api.image.ImageURL
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.openai.api.aichat.ChatApplication.Companion.app
import kotlinx.coroutines.flow.Flow

const val MAX_TOKENS = 4097 - 100 // 解决 token 本地计算误差问题

@BetaOpenAI
class NewChatViewModel(chatApp: Application) : AndroidViewModel(chatApp) {

    companion object {
        const val TAG = "NewChatViewModel"
    }

    private var openAI: OpenAI? = null

    private val messages = mutableListOf<ChatMessage>()

    init {
        messages.add(ChatMessage(ChatRole.System, ""))
    }

    fun resetMessages(prompt: String = "") {
        messages.clear()
        messages.add(ChatMessage(ChatRole.System, prompt))
    }

    fun addMessage(role: ChatRole, content: String) {
        val tokens = getTokensForText(content)
        val newMessage = ChatMessage(role, content)
        addMessageWithTokenCheck(messages, newMessage, tokens, MAX_TOKENS, 10)
    }

    private fun getOpenAI(): OpenAI? {
        if (openAI == null) {
            if (app.sharedPrefHelper.openAIAPIKey.isNotEmpty()) {
                openAI = OpenAI(
                    token = app.sharedPrefHelper.openAIAPIKey,
                )
            }
        }
        return openAI
    }

    fun getChatData(model: String): Flow<ChatCompletionChunk>? {
        Log.e(TAG, "getChatData model: $model")
        val chatCompletionRequest =
            ChatCompletionRequest(model = ModelId(model), messages = messages)
        return getOpenAI()?.chatCompletions(chatCompletionRequest)
    }

    suspend fun getImageData(content: String): List<ImageURL>? {
        return getOpenAI()?.imageURL( // or openAI.imageJSON
            creation = ImageCreation(
                prompt = content,
                n = 2,
                size = ImageSize.is1024x1024
            )
        )
    }

    private fun addMessageWithTokenCheck(
        messages: MutableList<ChatMessage>,
        newMessage: ChatMessage,
        newTokens: Int,
        maxTokens: Int,
        maxBacktrackMessages: Int,
    ): Boolean {
        var totalTokens = newTokens
        var checkedMessages = 0

        for (i in messages.indices.reversed()) {
            totalTokens += getTokensForText(newMessage.content)

            if (totalTokens > maxTokens) {
                messages.removeAt(i)
            } else {
                break
            }

            checkedMessages++
            if (checkedMessages >= maxBacktrackMessages) {
                break
            }
        }

        if (totalTokens <= maxTokens) {
            messages.add(newMessage)
            return true
        }
        return false
    }

    private fun getTokensForText(text: String): Int {
        val regex = Regex("[\u4e00-\u9fa5]|[\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{S}\\p{C}]")
        val tokens = regex.findAll(text)
        return tokens.count()
    }
}