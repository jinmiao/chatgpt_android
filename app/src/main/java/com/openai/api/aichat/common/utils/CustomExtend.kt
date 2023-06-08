package com.openai.api.aichat.common.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlin.reflect.KClass
import android.view.View
import com.openai.api.aichat.R

fun <T : Activity> Context.startActivitySafe(clazz: KClass<T>, block: Intent.() -> Unit = {}) {
    val intent = Intent(this, clazz.java).apply(block)
    try {
        startActivity(intent)
    } catch (e: Exception) {
        Log.e("startActivitySafe", "Cannot start activity", e)
    }
}

var View.lastClickTime: Long
    get() = if (getTag(R.id.last_click_time) != null) getTag(R.id.last_click_time) as Long else 0
    set(value) {
        setTag(R.id.last_click_time, value)
    }

fun View.setOnSingleClickListener(interval: Int = 1000, onClick: (View) -> Unit) {
    require(interval >= 0) { "Interval can't be negative" }

    setOnClickListener {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > interval) {
            lastClickTime = currentTime
            onClick.invoke(it)
        }
    }
}
