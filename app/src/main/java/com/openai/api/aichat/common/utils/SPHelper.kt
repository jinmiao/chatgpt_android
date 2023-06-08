package com.openai.api.aichat.common.utils

import android.content.Context
import android.content.SharedPreferences

// TODO Need add yours
const val OPENAI_API_KEY = ""

class SPHelper(val context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(SP_PREFS_NAME, Context.MODE_PRIVATE)

    var openAIAPIKey: String
        get() = sharedPreferences.getString(SP_OPENAI_API_KEY, OPENAI_API_KEY) ?: ""
        set(value) = sharedPreferences.edit().putString(SP_OPENAI_API_KEY, value).apply()

    var lastChatEmpty: Boolean
        get() = sharedPreferences.getBoolean(SP_KEY_LAST_CHAT_EMPTY, true)
        set(value) = sharedPreferences.edit().putBoolean(SP_KEY_LAST_CHAT_EMPTY, value).apply()


    companion object {
        private const val SP_PREFS_NAME = "role_chat_prefs"
        private const val SP_OPENAI_API_KEY = "openai_api_key"
        private const val SP_KEY_LAST_CHAT_EMPTY = "last_chat_empty"
    }
}
