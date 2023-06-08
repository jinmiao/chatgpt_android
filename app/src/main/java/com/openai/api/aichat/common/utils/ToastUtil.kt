package com.openai.api.aichat.common.utils

import android.content.Context
import android.widget.Toast

object ToastUtil {

    private var mToast: Toast? = null

    fun showTip(context: Context, str: String, short: Boolean = true) {
        if (mToast != null) {
            mToast?.cancel()
        }
        mToast = Toast.makeText(context, str, if (short) Toast.LENGTH_SHORT else Toast.LENGTH_LONG)
        mToast?.show()
    }
}