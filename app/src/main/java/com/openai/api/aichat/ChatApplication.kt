package com.openai.api.aichat

import android.app.Application
import com.openai.api.aichat.common.db.AppDatabase
import com.openai.api.aichat.common.db.DBRepository
import com.openai.api.aichat.common.utils.SPHelper

class ChatApplication : Application() {

    private val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val repository: DBRepository by lazy { DBRepository(database.chatDao(), database.messageDao()) }

    lateinit var sharedPrefHelper: SPHelper
        private set

    companion object {
        const val TAG = "APP"

        // 使用 lazy 委托属性实现线程安全的单例
        @Volatile
        private var _app: ChatApplication? = null

        val app: ChatApplication
            get() {
                return _app
                    ?: throw IllegalStateException("ChatApplication instance is not initialized yet.")
            }

    }

    override fun onCreate() {
        super.onCreate()
        _app = this
        sharedPrefHelper = SPHelper(this)
    }
}