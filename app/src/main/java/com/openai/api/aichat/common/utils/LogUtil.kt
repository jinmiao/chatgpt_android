package com.openai.api.aichat.common.utils

import android.util.Log
import com.openai.api.aichat.BuildConfig

object LogUtil {
    private const val TAG = "AIChat-"

    fun v(tag: String, log: String) {
        if (BuildConfig.DEBUG)
            Log.v(TAG + tag, log)
    }

    fun v(tag: String, log: String, e: Throwable) {
        if (BuildConfig.DEBUG)
            Log.v(TAG + tag, getInfo() + log, e)
    }

    fun d(tag: String, log: String) {
        if (BuildConfig.DEBUG)
            Log.d(TAG + tag, log)
    }

    fun d(tag: String, log: String, e: Throwable) {
        if (BuildConfig.DEBUG)
            Log.d(TAG + tag, getInfo() + log, e)
    }

    fun i(tag: String, log: String) {
        if (BuildConfig.DEBUG)
            Log.i(TAG + tag, log)
    }

    fun i(tag: String, log: String, e: Throwable) {
        if (BuildConfig.DEBUG)
            Log.i(TAG + tag, getInfo() + log, e)
    }

    fun w(tag: String, log: String) {
        if (BuildConfig.DEBUG)
            Log.w(TAG + tag, log)
    }

    fun w(tag: String, log: String, e: Throwable) {
        if (BuildConfig.DEBUG)
            Log.w(TAG + tag, getInfo() + log, e)
    }

    fun e(tag: String, log: String) {
        if (BuildConfig.DEBUG)
            Log.e(TAG + tag, log)
    }

    fun e(tag: String, log: String, e: Throwable) {
        if (BuildConfig.DEBUG)
            Log.e(TAG + tag, getInfo() + log, e)
    }

    private fun getInfo(): String {
        val lineBuffer = StringBuffer()
        val item = Thread.currentThread().stackTrace[4]
        val fileName = item.fileName
        val lineNum = item.lineNumber
        val className = item.className
        val funName = item.methodName
        lineBuffer.append("[")
        lineBuffer.append(fileName)
        lineBuffer.append(" ")
        lineBuffer.append(lineNum)
        lineBuffer.append("]")
        lineBuffer.append("(")
        lineBuffer.append(className)
        lineBuffer.append(" ")
        lineBuffer.append(funName)
        lineBuffer.append(") ")
        return lineBuffer.toString()
    }
}