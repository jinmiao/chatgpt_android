package com.openai.api.aichat.ui.home.history

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import cc.shinichi.library.ImagePreview
import com.openai.api.aichat.ChatApplication.Companion.app
import com.openai.api.aichat.common.model.Message
import com.openai.api.aichat.databinding.ActivityHistoryMessagesBinding
import com.openai.api.aichat.ui.base.BaseActivity
import com.openai.api.aichat.ui.home.newchat.MessageAdapter
import com.openai.api.aichat.ui.home.newchat.MessageAdapterCallbacks
import kotlinx.coroutines.launch
import kotlin.math.max

class HistoryMessagesActivity : BaseActivity<ActivityHistoryMessagesBinding, HistoryViewModel>() {

    companion object {
        const val TAG = "AIChatActivity"
    }

    override fun getViewBinding() = ActivityHistoryMessagesBinding.inflate(layoutInflater)

    override fun getViewModelClass() = HistoryViewModel::class.java

    override fun getToolBar() = binding.toolbar

    private var chatTitle = ""
    private var chatCID = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        chatTitle = intent.getStringExtra("title") ?: ""
        chatCID = intent.getStringExtra("cid") ?: ""
        if (chatTitle.isNotEmpty()) {
            title = chatTitle
        }
        initUIAndLoadData()
    }

    private lateinit var messageAdapter: MessageAdapter
    private fun initUIAndLoadData() {
        binding.recyclerView.apply {
            messageAdapter =
                MessageAdapter(mutableListOf(), object : MessageAdapterCallbacks {
                    override fun onReceiveImageClick(view: View, message: Message) {
                        try {
                            ImagePreview.instance.setContext(context).setIndex(0)
                                .setImage(message.images).start()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                })
            layoutManager = LinearLayoutManager(context)
            adapter = messageAdapter
        }

        lifecycleScope.launch {
            val list = app.repository.getMessagesForChat(chatCID)
            binding.recyclerView.visibility =
                if (list.isNotEmpty()) View.VISIBLE else View.INVISIBLE
            binding.emptyTv.visibility = if (list.isNotEmpty()) View.INVISIBLE else View.VISIBLE

            messageAdapter.setMessages(list.toMutableList())
            binding.recyclerView.scrollToPosition(max(0, messageAdapter.itemCount - 1))
        }
    }
}