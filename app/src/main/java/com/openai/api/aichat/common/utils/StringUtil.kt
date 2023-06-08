package com.openai.api.aichat.common.utils

import android.content.Context
import com.openai.api.aichat.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object StringUtil {

    fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return format.format(date)
    }

    fun formatTimestampStr(context: Context, timestamp: Long): String {
        val currentTime = System.currentTimeMillis()
        val diffTime = currentTime - timestamp

        val oneMinute = 60 * 1000
        val oneHour = 60 * oneMinute
        val oneDay = 24 * oneHour

        return when {
            diffTime < 10 * oneMinute -> context.getString(R.string.now)
            diffTime < 30 * oneMinute -> context.getString(R.string.in_30_mins)
            diffTime < oneHour -> context.getString(R.string.in_1_hour)
            diffTime < oneDay -> context.getString(R.string.today)
            diffTime < 2 * oneDay -> context.getString(R.string.yesterday)
            else -> {
                val sdf = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
                sdf.format(Date(timestamp))
            }
        }
    }
}