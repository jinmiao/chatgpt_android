package com.openai.api.aichat.common.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.openai.api.aichat.common.model.Chat
import com.openai.api.aichat.common.model.Message

@Database(entities = [Chat::class, Message::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext,
                    AppDatabase::class.java,
                    "db_messages").fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
