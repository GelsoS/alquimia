package com.alquimia.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alquimia.data.remote.models.ConversationData
import com.alquimia.databinding.ItemConversationBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class ConversationAdapter(private val onItemClicked: (ConversationData) -> Unit) :
    ListAdapter<ConversationData, ConversationAdapter.ConversationViewHolder>(ConversationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val binding = ItemConversationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ConversationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val conversation = getItem(position)
        holder.bind(conversation)
    }

    inner class ConversationViewHolder(private val binding: ItemConversationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onItemClicked(getItem(adapterPosition))
            }
        }

        fun bind(conversation: ConversationData) {
            binding.apply {
                tvOtherUserName.text = conversation.otherUser?.name ?: "Usuário Desconhecido"
                tvLastMessage.text = conversation.lastMessage ?: "Nenhuma mensagem"
                tvLastMessageTime.text = formatTimeAgo(conversation.lastMessageTime)

                conversation.otherUser?.profilePicture?.let { imageUrl ->
                    Glide.with(ivProfilePicture.context)
                        .load(imageUrl)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .circleCrop()
                        .placeholder(android.R.drawable.sym_def_app_icon) // Placeholder padrão
                        .error(android.R.drawable.sym_def_app_icon) // Imagem de erro
                        .into(ivProfilePicture)
                } ?: run {
                    ivProfilePicture.setImageResource(android.R.drawable.sym_def_app_icon)
                }
            }
        }

        private fun formatTimeAgo(timestamp: String?): String {
            // Implemente sua lógica de formatação de tempo aqui
            // Por exemplo, "5 min atrás", "Ontem", "12/07/2025"
            // Por simplicidade, retornaremos o timestamp bruto por enquanto
            return timestamp?.substring(0, 10) ?: "" // Apenas a data
        }
    }

    class ConversationDiffCallback : DiffUtil.ItemCallback<ConversationData>() {
        override fun areItemsTheSame(oldItem: ConversationData, newItem: ConversationData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ConversationData, newItem: ConversationData): Boolean {
            return oldItem == newItem
        }
    }
}
