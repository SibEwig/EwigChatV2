package com.sibewig.ewigchatv2.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sibewig.ewigchatv2.databinding.ItemMessageIncomingBinding
import com.sibewig.ewigchatv2.databinding.ItemMessageOutgoingBinding
import com.sibewig.ewigchatv2.presentation.chat.model.MessageUi

class MessageAdapter : ListAdapter<MessageUi, MessageAdapter.MessageViewHolder>(
    MessageDiffCallback
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MessageViewHolder {
        return when (viewType) {
            INCOMING_MESSAGE_VIEW_TYPE -> {
                val binding = ItemMessageIncomingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                MessageViewHolder.Incoming(binding)
            }

            OUTGOING_MESSAGE_VIEW_TYPE -> {
                val binding = ItemMessageOutgoingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                MessageViewHolder.Outgoing(binding)
            }

            else -> error("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(
        holder: MessageViewHolder,
        position: Int
    ) {
        val message = currentList[position]
        when (holder) {
            is MessageViewHolder.Incoming -> {
                holder.binding.tvMessage.text = message.text
            }

            is MessageViewHolder.Outgoing -> {
                holder.binding.tvMessage.text = message.text
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = currentList[position]
        return if (message.isMine) OUTGOING_MESSAGE_VIEW_TYPE else INCOMING_MESSAGE_VIEW_TYPE
    }

    sealed class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        class Outgoing(
            val binding: ItemMessageOutgoingBinding
        ) : MessageViewHolder(binding.root)

        class Incoming(
            val binding: ItemMessageIncomingBinding
        ) : MessageViewHolder(binding.root)

    }

    companion object {

        private const val INCOMING_MESSAGE_VIEW_TYPE = 100
        private const val OUTGOING_MESSAGE_VIEW_TYPE = 101

    }
}