package com.alquimia.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alquimia.data.remote.models.MessageData
import com.alquimia.databinding.ItemMessageReceivedBinding
import com.alquimia.databinding.ItemMessageSentBinding

class MessageAdapter(private val currentUserId: String?) :
    ListAdapter<MessageData, RecyclerView.ViewHolder>(MessageDiffCallback()) {

    private val VIEW_TYPE_SENT = 1
    private val VIEW_TYPE_RECEIVED = 2

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        return if (message.senderId == currentUserId) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val binding = ItemMessageSentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            SentMessageViewHolder(binding)
        } else {
            val binding = ItemMessageReceivedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ReceivedMessageViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        if (holder.itemViewType == VIEW_TYPE_SENT) {
            (holder as SentMessageViewHolder).bind(message)
        } else {
            (holder as ReceivedMessageViewHolder).bind(message)
        }
    }

    inner class SentMessageViewHolder(private val binding: ItemMessageSentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: MessageData) {
            binding.tvMessageContent.text = message.content
            binding.tvMessageTime.text = formatTime(message.timestamp)
        }
    }

    inner class ReceivedMessageViewHolder(private val binding: ItemMessageReceivedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: MessageData) {
            binding.tvMessageContent.text = message.content
            binding.tvMessageSender.text = message.senderName ?: "Desconhecido"
            binding.tvMessageTime.text = formatTime(message.timestamp)
        }
    }

    class MessageDiffCallback : DiffUtil.ItemCallback<MessageData>() {
        override fun areItemsTheSame(oldItem: MessageData, newItem: MessageData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MessageData, newItem: MessageData): Boolean {
            return oldItem == newItem
        }
    }

    private fun formatTime(timestamp: String): String {
        // Implemente sua lógica de formatação de tempo aqui (ex: "HH:mm")
        // Por simplicidade, retornaremos apenas a hora e minuto
        return try {
            timestamp.substring(11, 16) // Assume formato ISO 8601 "YYYY-MM-DDTHH:mm:ss.sssZ"
        } catch (e: Exception) {
            timestamp // Retorna o original em caso de erro
        }
    }
}
