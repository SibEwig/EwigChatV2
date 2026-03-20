package com.sibewig.ewigchatv2.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sibewig.ewigchatv2.databinding.ItemChatBinding
import com.sibewig.ewigchatv2.presentation.chats.model.ChatUi
import com.sibewig.ewigchatv2.presentation.extensions.toMessageTime

class ChatAdapter : ListAdapter<ChatUi, ChatAdapter.ChatViewHolder>(ChatDiffCallback) {

    var onChatClickListener: ((String) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatViewHolder {
        val binding = ItemChatBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ChatViewHolder,
        position: Int
    ) {
        val chat = currentList[position]
        with(holder.binding) {
            textViewContactName.text = chat.interlocutorName
            textViewLastMessage.text = chat.lastMessage?.text
            textViewTime.text = chat.lastMessage?.timestamp?.toMessageTime()
            containerChat.setOnClickListener {
                onChatClickListener?.invoke(chat.id)
            }
        }
    }

    class ChatViewHolder(val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root)

}