package com.openai.api.aichat.ui.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import cc.shinichi.library.ImagePreview
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.openai.api.aichat.ChatApplication.Companion.app
import com.openai.api.aichat.databinding.ActivitySettingsBinding
import com.openai.api.aichat.ui.base.BaseActivity
import java.util.*

class SettingsActivity : BaseActivity<ActivitySettingsBinding, SettingsViewModel>() {

    override fun getViewBinding(): ActivitySettingsBinding {
        return ActivitySettingsBinding.inflate(layoutInflater)
    }

    override fun getViewModelClass(): Class<SettingsViewModel> {
        return SettingsViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initUI()
    }

    @SuppressLint("CheckResult")
    private fun initUI() {
        val key = app.sharedPrefHelper.openAIAPIKey
        binding.openaiKey.text = if (key.isNullOrEmpty()) "null" else key
        binding.apiKeySettings.setOnClickListener {
            setAPIKey {
                app.sharedPrefHelper.openAIAPIKey = it
                binding.openaiKey.text = it
            }
        }

        binding.shareApp.setOnClickListener {
            shareApp(this)
        }

        binding.downloadApp.setOnClickListener {
            downloadApp()
        }
    }

    @SuppressLint("CheckResult")
    private fun setAPIKey(cb: (String) -> Unit) {
        MaterialDialog(this).show {
            title(text = "输入你的 OpenAI API Key")
            message(text = "安全提示：Key 只会存在本地，不会上传到服务器。")
            input(hint = "openai api key") { dialog, text ->
                cb.invoke(text.toString())
            }
        }
    }

    private fun shareApp(context: Context) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT,
                "私人定制 AI，快来下载体验吧。\nhttps://www.pgyer.com/customai")
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }

    private fun downloadApp() {
        val url = "https://www.pgyer.com/Fq458k"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    override fun getToolBar(): Toolbar {
        return binding.toolbar
    }
}