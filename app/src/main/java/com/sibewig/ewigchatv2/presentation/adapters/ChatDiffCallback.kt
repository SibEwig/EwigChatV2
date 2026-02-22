package com.sibewig.ewigchatv2.presentation.adapters

import androidx.recyclerview.widget.DiffUtil
import com.sibewig.ewigchatv2.domain.entity.Chat

object ChatDiffCallback: DiffUtil.ItemCallback<Chat>() {

    override fun areItemsTheSame(
        oldItem: Chat,
        newItem: Chat
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: Chat,
        newItem: Chat
    ): Boolean {
        return oldItem == newItem
    }
}