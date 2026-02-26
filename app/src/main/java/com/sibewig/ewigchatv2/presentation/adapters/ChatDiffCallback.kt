package com.sibewig.ewigchatv2.presentation.adapters

import androidx.recyclerview.widget.DiffUtil
import com.sibewig.ewigchatv2.presentation.chats.model.ChatUi

object ChatDiffCallback: DiffUtil.ItemCallback<ChatUi>() {

    override fun areItemsTheSame(
        oldItem: ChatUi,
        newItem: ChatUi
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: ChatUi,
        newItem: ChatUi
    ): Boolean {
        return oldItem == newItem
    }
}