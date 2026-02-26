package com.sibewig.ewigchatv2.presentation.adapters

import androidx.recyclerview.widget.DiffUtil
import com.sibewig.ewigchatv2.presentation.chat.model.MessageUi

object MessageDiffCallback: DiffUtil.ItemCallback<MessageUi>() {

    override fun areItemsTheSame(
        oldItem: MessageUi,
        newItem: MessageUi
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: MessageUi,
        newItem: MessageUi
    ): Boolean {
        return oldItem == newItem
    }
}