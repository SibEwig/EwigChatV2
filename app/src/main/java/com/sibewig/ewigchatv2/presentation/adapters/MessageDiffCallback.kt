package com.sibewig.ewigchatv2.presentation.adapters

import androidx.recyclerview.widget.DiffUtil
import com.sibewig.ewigchatv2.presentation.chat.model.MessageUI

object MessageDiffCallback: DiffUtil.ItemCallback<MessageUI>() {

    override fun areItemsTheSame(
        oldItem: MessageUI,
        newItem: MessageUI
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: MessageUI,
        newItem: MessageUI
    ): Boolean {
        return oldItem == newItem
    }
}