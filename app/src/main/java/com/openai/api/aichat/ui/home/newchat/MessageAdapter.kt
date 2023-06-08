package com.openai.api.aichat.ui.home.newchat

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.openai.api.aichat.R
import com.openai.api.aichat.common.model.Message
import com.openai.api.aichat.common.utils.StringUtil
import com.openai.api.aichat.databinding.ItemMessageBinding
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.core.MarkwonTheme
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.ext.tables.TableTheme
import io.noties.markwon.ext.tasklist.TaskListPlugin
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin

class MessageAdapter(
    private val messages: MutableList<Message>,
    private val callbacks: MessageAdapterCallbacks?,
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    companion object {
        private const val TAG = "ChatAdapter"
    }

    inner class MessageViewHolder(private val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message, position: Int) {
            if (!message.isAI) {
                currentUserMessage(message, binding)
            } else {
                receiveUserMessage(message, binding, position)
            }
        }

        fun updateContent(message: Message, position: Int) {
            if (!message.isAI) {
                currentUserMessage(message, binding)
            } else {
                receiveUserMessage(message, binding, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position], position)
    }

    override fun onBindViewHolder(
        holder: MessageViewHolder,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        if (payloads.isEmpty()) {
            // 如果没有有效负载，进行完全绑定
            super.onBindViewHolder(holder, position, payloads)
        } else {
            // 根据负载类型执行局部更新
            for (payload in payloads) {
                when (payload) {
                    PayloadType.CONTENT -> holder.updateContent(messages[position], position)
                }
            }
        }
    }

    override fun getItemCount() = messages.size

    @SuppressLint("NotifyDataSetChanged")
    fun setMessages(list: MutableList<Message>) {
        messages.clear()
        messages.addAll(list)
        notifyDataSetChanged()
    }

    fun addMessage(message: Message) {
        // 判断是否已经存在
        val position = messages.indexOfFirst { it.mid == message.mid }
        if (position >= 0) {
            messages[position] = message
            notifyItemChanged(position, PayloadType.CONTENT)
        } else {
            messages.add(message)
            notifyItemInserted(messages.size - 1)
        }
    }

    private fun currentUserMessage(message: Message, binding: ItemMessageBinding) {
        binding.receiveLayout.visibility = View.GONE
        binding.sendLayout.visibility = View.VISIBLE
        binding.messageContentTextView.setTextIsSelectable(true)
        binding.messageContentTextView.text = message.content
        binding.messageTimestampTextView.text =
            StringUtil.formatTimestampStr(binding.root.context, message.timestamp)
    }

    private fun receiveUserMessage(
        message: Message,
        binding: ItemMessageBinding,
        position: Int,
        update: Boolean = false,
    ) {
        binding.receiveLayout.visibility = View.VISIBLE
        binding.sendLayout.visibility = View.GONE

        if (message.images.isNotEmpty()) {
            binding.receiveImage.visibility = View.VISIBLE
            binding.messageTextViewReceive.visibility = View.GONE

            Glide.with(binding.receiveImage.context).load(message.images)
                .placeholder(R.drawable.bg_new_chat_tip)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(12)))
                .into(binding.receiveImage)

            binding.receiveImage.setOnClickListener {
                callbacks?.onReceiveImageClick(it, message)
            }

        } else {
            binding.receiveImage.visibility = View.GONE
            binding.messageTextViewReceive.visibility = View.VISIBLE
            binding.messageContentTextViewReceive.visibility = View.VISIBLE
            binding.progressBar.visibility =
                if (message.content.isNotEmpty()) View.INVISIBLE else View.VISIBLE

            val textview = binding.messageContentTextViewReceive
            val content = message.content
            try {
                textview.setTextIsSelectable(true)
                val themeplugin = object : AbstractMarkwonPlugin() {

                    // 主题
                    override fun configureTheme(builder: MarkwonTheme.Builder) {
                        builder.codeBlockBackgroundColor(textview.context.getColor(R.color.messageCodeBlockBackgroundColor))
                        builder.codeBlockTextColor(textview.context.getColor(R.color.messageCodeBlockTextColor))

                        builder.codeTextColor(textview.context.getColor(R.color.messageCodeTextColor))
                        builder.codeBackgroundColor(textview.context.getColor(R.color.messageCodeBackgroundColor))
                        builder.isLinkUnderlined(true)
                    }

                    //
                    override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {
                        builder.linkResolver { view, link ->
                            // react to link click here
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                            textview.context.startActivity(intent)
                        }
                    }
                }

                // table
                val tablePlugin = TablePlugin.create(TableTheme.Builder()
                    .tableBorderColor(textview.context.getColor(R.color.gray_darkest))
                    .tableBorderWidth(0).tableCellPadding(0)
                    .tableHeaderRowBackgroundColor(textview.context.getColor(R.color.gray_darkest))
                    .tableEvenRowBackgroundColor(textview.context.getColor(R.color.gray_dark))
                    .tableOddRowBackgroundColor(textview.context.getColor(R.color.gray_dark_transparent))
                    .build())
                val markwon = Markwon.builder(textview.context).usePlugin(themeplugin)
//                .usePlugin(LinkifyPlugin.create())
                    .usePlugin(tablePlugin)
                    // use TableAwareLinkMovementMethod to handle clicks inside tables,
                    // wraps LinkMovementMethod internally
//                .usePlugin(MovementMethodPlugin.create(TableAwareMovementMethod.create()))
                    .usePlugin(MarkwonInlineParserPlugin.create())
                    .usePlugin(StrikethroughPlugin.create())
                    .usePlugin(TaskListPlugin.create(textview.context)).build()

                val node = markwon.parse(content)
                val markdown = markwon.render(node)
                markwon.setParsedMarkdown(textview, markdown)
            } catch (e: Exception) {
                textview.text = content
            }
        }
    }
}

interface MessageAdapterCallbacks {
    fun onReceiveImageClick(view: View, message: Message)
}

enum class PayloadType {
    CONTENT
}
