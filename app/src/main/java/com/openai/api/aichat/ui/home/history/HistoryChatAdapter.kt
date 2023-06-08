package com.openai.api.aichat.ui.home.history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.openai.api.aichat.common.model.Chat
import com.openai.api.aichat.common.utils.LogUtil
import com.openai.api.aichat.common.utils.StringUtil
import com.openai.api.aichat.common.utils.setOnSingleClickListener
import com.openai.api.aichat.databinding.ItemChatBinding

class HistoryChatAdapter(
    private val onItemClickListener: (item: Chat) -> Unit,
    private val onItemLongClickListener: (item: Chat, position: Int) -> Unit,
) : RecyclerView.Adapter<HistoryChatAdapter.ViewHolder>() {

    companion object {
        const val TAG = "ChatsAdapter"
    }

    private val items: MutableList<Chat> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun updateChatList(data: List<Chat>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    fun removeChat(chat: Chat) {
        try {
            val index = items.indexOf(chat)
            if (index >= 0) {
                items.remove(chat)
                notifyItemRemoved(index)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class ViewHolder(private val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(item: Chat) {
            // Load avatar
//            Glide.with(binding.avatar.context)
//                .load(item.avatar)
//                .placeholder(R.drawable.ic_chatgpt)
//                .circleCrop()
//                .into(binding.avatar)

            binding.imageType.visibility = if (item.apiType == "image") View.VISIBLE else View.GONE
            binding.title.text = item.title
            binding.description.text = item.lastMessage
            binding.timestamp.text =
                StringUtil.formatTimestampStr(binding.root.context, item.timestamp)
            binding.modelLabel.visibility =
                if ("gpt-4".equals(item.model, true)) View.VISIBLE else View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)

        // 设置点击事件
        holder.itemView.setOnSingleClickListener {
            LogUtil.d(TAG, "onBindViewHolder position: $position")
            onItemClickListener(item)
        }

        holder.itemView.setOnLongClickListener {
            onItemLongClickListener(item, position)
            true
        }
    }

    override fun getItemCount() = items.size
}