package com.sibewig.ewigchatv2.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sibewig.ewigchatv2.databinding.ItemChatBinding
import com.sibewig.ewigchatv2.domain.entity.Chat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatAdapter : ListAdapter<Chat, ChatAdapter.ChatViewHolder>(ChatDiffCallback) {

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
            textViewContactName.text = chat.interlocutor.name.take(15)
            textViewLastMessage.text = chat.lastMessage?.text
            textViewTime.text = chat.lastMessage?.timestamp?.toChatTime()
        }
    }

    class ChatViewHolder(val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root)

}


fun Long.toChatTime(): String {
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    return formatter.format(Date(this))
}