package com.openai.api.aichat.common.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Chat(
    @PrimaryKey var cid: String,
    var title: String = "",
    var des: String = "",
    var prompt: String = "",
    var lastMessage: String = "",
    var tts: String = "",
    var ttsLang: String = "",
    var timestamp: Long = System.currentTimeMillis(),
    var model: String = "",
    var apiType: String = "", // chat, image, audio, file, edit
)

@Entity
data class Message(
    @PrimaryKey var mid: String,    // 使用 时间戳当 id
    var cid: String = "",           // chat id
    var content: String = "",
    var timestamp: Long = System.currentTimeMillis(),
    var isStop: Boolean = false,
    var isAI: Boolean = false,
    var images: String = "",
)


data class UserInfo(
    var uid: String = "",
    val name: String = "",
    var phone: String = "",
    val orderId: String = "",
    val gpt3: Int = 10,
    val gpt4: Int = 2,
    val speech: Int = 10,
)

