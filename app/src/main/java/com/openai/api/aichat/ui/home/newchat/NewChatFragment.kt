package com.openai.api.aichat.ui.home.newchat

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cc.shinichi.library.ImagePreview
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatRole
import com.openai.api.aichat.ChatApplication.Companion.app
import com.openai.api.aichat.common.model.Chat
import com.openai.api.aichat.common.model.Message
import com.openai.api.aichat.common.utils.LogUtil
import com.openai.api.aichat.common.utils.ToastUtil
import com.openai.api.aichat.common.utils.setOnSingleClickListener
import com.openai.api.aichat.databinding.FragmentNewChatBinding
import com.openai.api.aichat.ui.home.MainBaseFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.max

@BetaOpenAI
class NewChatFragment : MainBaseFragment<FragmentNewChatBinding, NewChatViewModel>() {

    override fun getViewBinding(): FragmentNewChatBinding {
        return FragmentNewChatBinding.inflate(layoutInflater)
    }

    override fun getViewModelClass(): Class<NewChatViewModel> {
        return NewChatViewModel::class.java
    }

    companion object {
        private const val TAG = "NewChatFragment"
    }

    // GPT 结果
    private val gptBuffer = StringBuffer()

    @Volatile
    private var apiLoading = false

    @Volatile
    private var chatImageStart = false

    // chat adapter new ui
    private lateinit var messageAdapter: MessageAdapter

    private var aiModel = "gpt-3.5-turbo"
    private var isGPT4 = false

    @Volatile
    private var apiType = "chat" // chat, image
    private var chatId = System.currentTimeMillis().toString()
    private var syncChat = Chat(chatId, apiType = apiType)
    private val syncMessages: CopyOnWriteArrayList<Message> = CopyOnWriteArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUIAction()
        initSampleAction()

        // load data，加载最后一个
        loadLastChat()
    }

    private fun loadLastChat() {
        if (!app.sharedPrefHelper.lastChatEmpty) {
            activity?.lifecycleScope?.launch {
                val chat = app.repository.getLastChat()
                if (chat?.cid?.isNotEmpty() == true) {
                    chatId = chat.cid
                    syncChat = Chat(chatId, chat.apiType)
                    chatImageStart = chat.apiType == "image"
                    val messages = app.repository.getMessagesForChat(chat.cid)
                    if (messages.isNotEmpty()) {
                        binding.sampleLayoutImage.visibility = View.INVISIBLE
                        binding.sampleLayout.visibility = View.INVISIBLE
                        binding.recyclerView.visibility = View.VISIBLE
                        messageAdapter.setMessages(messages.toMutableList())
                        binding.recyclerView.scrollToPosition(maxOf(0, messages.size - 1))

                        // 设置上下文
                        messages.forEach {
                            if (it.isAI) {
                                viewModel.addMessage(ChatRole.Assistant, it.content)
                            } else {
                                viewModel.addMessage(ChatRole.User, it.content)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initSampleAction() {
        binding.sample1.setOnSingleClickListener {
            binding.messageEditText.setText(binding.sample1.text)
        }
        binding.sample2.setOnSingleClickListener {
            binding.messageEditText.setText(binding.sample2.text)
        }
        binding.sample3.setOnSingleClickListener {
            binding.messageEditText.setText(binding.sample3.text)
        }
        binding.sample4.setOnSingleClickListener {
            binding.messageEditText.setText(binding.sample4.text)
        }
        binding.sample5.setOnSingleClickListener {
            binding.messageEditText.setText(binding.sample5.text)
        }
        binding.sample6.setOnSingleClickListener {
            binding.messageEditText.setText(binding.sample6.text)
        }
    }

    private fun initUIAction() {
        binding.recyclerView.apply {
            messageAdapter =
                MessageAdapter(mutableListOf(), object : MessageAdapterCallbacks {
                    override fun onReceiveImageClick(view: View, message: Message) {
                        try {
                            ImagePreview.instance.setContext(requireContext()).setIndex(0)
                                .setImage(message.images).start()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                })
            layoutManager = LinearLayoutManager(requireContext())
            adapter = messageAdapter
        }

        // 监听recyclerview 滑动事件
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        // 停止滚动
                    }
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        // 开始拖动滚动
                        hideInputMethod()
                    }
                    RecyclerView.SCROLL_STATE_SETTLING -> {
                        // 滚动自动设置
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // 在此处处理滚动事件
                // dx 和 dy 分别表示水平和垂直方向上的滚动增量
                // 可根据需要进行相应的处理
            }
        })

        // 发送消息
        binding.sendButton.setOnClickListener {
            val messageContent = binding.messageEditText.text.toString().trim()
            if (messageContent.isNotEmpty()) {
                if (apiLoading) {
                    ToastUtil.showTip(requireContext(), "AI 回复中...")
                } else {
                    inputMessage(messageContent)
                }
            }
        }

        // 新建聊天
        activityModel.newChat.observe(viewLifecycleOwner) {
            if (it) {
                chatImageStart = false
                binding.recyclerView.visibility = View.INVISIBLE
                binding.sampleLayoutImage.visibility = View.INVISIBLE
                binding.sampleLayout.visibility = View.VISIBLE
                binding.sampleAiDes.visibility = View.GONE

                openaiJob?.cancel()
                openaiJob = null
                apiLoading = false
                chatId = System.currentTimeMillis().toString()
                syncChat = Chat(chatId, apiType = "chat")
                syncMessages.clear()

                messageAdapter.setMessages(mutableListOf())
                viewModel.resetMessages()
                activityModel.newChat.value = false
                app.sharedPrefHelper.lastChatEmpty = true
            }
        }

        // 开始绘画
        activityModel.newChatForImage.observe(viewLifecycleOwner) {
            if (it) {
                chatImageStart = true
                binding.sampleLayoutImage.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.INVISIBLE
                binding.sampleLayout.visibility = View.INVISIBLE
                binding.sampleAiDes.visibility = View.GONE

                openaiJob?.cancel()
                openaiJob = null
                apiLoading = false
                chatId = System.currentTimeMillis().toString()
                syncChat = Chat(chatId, apiType = "image")
                syncMessages.clear()

                messageAdapter.setMessages(mutableListOf())
                viewModel.resetMessages()
                activityModel.newChat.value = false
                app.sharedPrefHelper.lastChatEmpty = true
            }
        }

        // 监听drawerOpened
        activityModel.drawerOpened.observe(viewLifecycleOwner) {
            if (it) {
                hideInputMethod()
            }
        }
    }

    // 文字输入
    private fun inputMessage(messageContent: String) {
        if (app.sharedPrefHelper.openAIAPIKey.isNullOrEmpty()) {
            ToastUtil.showTip(requireContext(), "请先设置 Openai API Key！")
            return
        }
        // 隐藏输入法
        hideInputMethod()
        // input 输入
        val newInputMessage = Message(mid = System.currentTimeMillis().toString(),
            cid = chatId,
            content = messageContent,
            timestamp = System.currentTimeMillis(),
            isStop = false,
            isAI = false)

        syncChat.title = messageContent.take(50)
        syncMessages.add(newInputMessage)

        // Add the new message to the list and update the RecyclerView
        messageAdapter.addMessage(newInputMessage)
        binding.recyclerView.scrollToPosition(max(0, messageAdapter.itemCount - 1))
        // Clear the input field
        binding.messageEditText.setText("")
        if (binding.recyclerView.visibility == View.INVISIBLE || binding.recyclerView.visibility == View.GONE) {
            binding.recyclerView.visibility = View.VISIBLE
            binding.sampleLayout.visibility = View.INVISIBLE
            binding.sampleLayoutImage.visibility = View.INVISIBLE
        }

        if (chatImageStart) {
            openaiAPIImage(messageContent)
        } else {
            // 请求 openai api: chat
            openaiAPIChat(messageContent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        openaiJob?.cancel()
        openaiJob = null
        apiLoading = false
    }

    var openaiJob: Job? = null

    // image
    private fun openaiAPIImage(content: String) {
        val time = System.currentTimeMillis().toString()
        var imageReceive = Message(mid = time,
            cid = chatId,
            content = "",
            timestamp = System.currentTimeMillis(),
            isStop = false,
            images = "",
            isAI = true)
        messageAdapter.addMessage(imageReceive)

        openaiJob = lifecycleScope.launch {
            try {
                viewModel.getImageData(content)?.let { it ->
                    imageReceive = Message(mid = time,
                        cid = chatId,
                        content = "",
                        timestamp = System.currentTimeMillis(),
                        isStop = true,
                        images = it[0].url,
                        isAI = true)
                    messageAdapter.addMessage(imageReceive)

                    // 同步数据到 本地数据库
                    syncChat.lastMessage = it[0].url
                    syncChat.timestamp = System.currentTimeMillis()
                    syncMessages.add(imageReceive)
                    syncMessages(syncChat, syncMessages)
                    app.sharedPrefHelper.lastChatEmpty = false
                }
            } catch (e: Exception) {
                apiLoading = false
                openaiJob?.cancel()
                openaiJob = null

                // 切换到主线程更新UI
                withContext(Dispatchers.Main) {
                    // UI toast 提示
                    ToastUtil.showTip(requireContext(),
                        "Openai api error, Please try again later.",
                        false)

                    // error content
                    messageAdapter.addMessage(Message(mid = System.currentTimeMillis().toString(),
                        cid = chatId,
                        content = "${gptBuffer}[Error]",
                        timestamp = System.currentTimeMillis(),
                        isStop = true,
                        isAI = true))
                }

                e.printStackTrace()
            }
        }
    }

    // chat
    private fun openaiAPIChat(content: String) {
        try {
            viewModel.addMessage(ChatRole.User, content)
            gptBuffer.setLength(0)

            val msgId = System.currentTimeMillis().toString()

            // loading
            val newReceiveMessage = Message(mid = msgId,
                cid = chatId,
                content = "",
                timestamp = 0L,
                isStop = false,
                isAI = true)
            // Add the new message to the list and update the RecyclerView
            messageAdapter.addMessage(newReceiveMessage)

            openaiJob = lifecycleScope.launch(Dispatchers.IO) {
//                LogUtil.e(TAG, "chat 提问：$content, apiLoading: $apiLoading")
                if (apiLoading) {
                    return@launch
                }
                apiLoading = true

                try {
                    viewModel.getChatData(aiModel)?.collect {
                        withContext(Dispatchers.Main) {
                            val id = msgId //it.choices[0].index
                            val r = it.choices[0].delta?.content
                            val finish = it.choices[0].finishReason
//                            LogUtil.e(TAG,
//                                "chat openai 返回 id: $id,  finish: $finish, content: $r, gptBuffer: $gptBuffer")
                            if ((r == null || r == "null") && (finish == null || finish == "null")) {
                                gptBuffer.setLength(0)
                            }
                            if (r != null && r != "null") {
                                gptBuffer.append(r)
                            }
                            responseMessage(id, gptBuffer.toString(), "stop".equals(finish, true))
                        }
                    }
                } catch (e: Exception) {
                    apiLoading = false
                    openaiJob?.cancel()
                    openaiJob = null

                    // 切换到主线程更新UI
                    withContext(Dispatchers.Main) {
                        // UI toast 提示
                        ToastUtil.showTip(requireContext(),
                            "Openai api error, Please try again later.",
                            false)

                        // error content
                        messageAdapter.addMessage(Message(mid = msgId,
                            cid = chatId,
                            content = "${gptBuffer}[Error]",
                            timestamp = System.currentTimeMillis(),
                            isStop = true,
                            isAI = true))
                    }

                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            apiLoading = false
        }
    }

    private suspend fun responseMessage(msgId: String, response: String, isStop: Boolean) {
        // 获取到结果之后切换到主线程
        withContext(Dispatchers.Main) {
            // gpt 回答结果返回
//            LogUtil.e(TAG, "chat openai gptBuffer：$response")
            // response
            val timestamp = System.currentTimeMillis()
            val newReceiveMessage = Message(mid = msgId,
                cid = chatId,
                content = if (isStop) response else response.plus("..."),
                timestamp = timestamp,
                isStop = isStop,
                isAI = true)
            // Add the new message to the list and update the RecyclerView
            messageAdapter.addMessage(newReceiveMessage)

            // TODO 同步数据到 本地数据库
            syncChat.lastMessage = response.take(50)
            syncChat.timestamp = timestamp
            syncMessages.add(newReceiveMessage)
            syncMessages(syncChat, syncMessages)
            app.sharedPrefHelper.lastChatEmpty = false
            if (isStop) {
                viewModel.addMessage(ChatRole.Assistant, response)
                val position = messageAdapter.itemCount - 1
                scrollToBottomOfItem(binding.recyclerView, position)

                apiLoading = false
                LogUtil.e(TAG, "......END......")
            }
        }
    }

    // 同步数据
    private fun syncMessages(chat: Chat, messages: CopyOnWriteArrayList<Message>) {
        lifecycleScope.launch {
            app.repository.insertChat(chat)
            app.repository.insertMessages(messages)
            syncMessages.clear()
        }
    }

    private fun scrollToBottomOfItem(recyclerView: RecyclerView, position: Int) {
        try {
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val view = layoutManager.findViewByPosition(position)
            if (view != null) {
                val itemHeight = view.height
                val recyclerViewHeight = recyclerView.height
                if (itemHeight > recyclerViewHeight) {
                    val topMargin = itemHeight - recyclerViewHeight
                    layoutManager.scrollToPositionWithOffset(position, -topMargin)
                } else {
                    layoutManager.scrollToPosition(position)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            recyclerView.smoothScrollToPosition(position)
        }
    }

    private fun hideInputMethod() {
        try {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (imm.isActive) {
                binding.messageEditText.clearFocus()
                imm.hideSoftInputFromWindow(binding.messageEditText.windowToken, 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showInputMethod() {
        try {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.messageEditText, InputMethodManager.SHOW_IMPLICIT)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}