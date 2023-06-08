package com.openai.api.aichat.ui.home.history

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.aallam.openai.api.BetaOpenAI
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.openai.api.aichat.ChatApplication.Companion.app
import com.openai.api.aichat.common.utils.startActivitySafe
import com.openai.api.aichat.databinding.ActivityHistoryChatBinding
import com.openai.api.aichat.ui.base.BaseActivity
import kotlinx.coroutines.launch

@BetaOpenAI
class HistoryChatActivity : BaseActivity<ActivityHistoryChatBinding, HistoryViewModel>() {

    override fun getToolBar(): Toolbar {
        return binding.toolbar
    }

    override fun getViewBinding(): ActivityHistoryChatBinding {
        return ActivityHistoryChatBinding.inflate(layoutInflater)
    }

    override fun getViewModelClass(): Class<HistoryViewModel> {
        return HistoryViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initUIAndLoadData()
    }

    private var deleteDialog: MaterialDialog? = null

    private lateinit var chatAdapter: HistoryChatAdapter

    @SuppressLint("CheckResult")
    private fun initUIAndLoadData() {
        chatAdapter = HistoryChatAdapter(
            //  点击
            {
                startActivitySafe(HistoryMessagesActivity::class) {
                    this.putExtra("title", it.title)
                    this.putExtra("cid", it.cid)
                }
            },
            // 长按 -> 删除
            { chat, position ->
                if (deleteDialog == null) {
                    deleteDialog = MaterialDialog(this)
                }
                deleteDialog?.show {
                    listItems(items = listOf("点击删除: ${chat.title}")) { dialog, index, text ->
                        lifecycleScope.launch {
                            app.repository.deleteChat(chat.cid)
                            chatAdapter.removeChat(chat)

                            if (chatAdapter.itemCount <= 0) {
                                binding.recyclerView.visibility = View.INVISIBLE
                                binding.emptyTv.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            })


        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context).apply {
                // 为 RecyclerView 添加垂直分隔线
                addItemDecoration(DividerItemDecoration(context, orientation))
            }
            adapter = chatAdapter
        }

        // 加载数据库数据
        lifecycleScope.launch {
            val list = app.repository.getAllChats()
            binding.recyclerView.visibility =
                if (list.isNotEmpty()) View.VISIBLE else View.INVISIBLE
            binding.emptyTv.visibility = if (list.isNotEmpty()) View.INVISIBLE else View.VISIBLE

            chatAdapter.updateChatList(list)
        }
    }
}