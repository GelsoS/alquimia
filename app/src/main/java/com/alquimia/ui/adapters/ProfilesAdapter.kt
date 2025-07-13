package com.alquimia.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alquimia.data.remote.models.UserData // Importar UserData
import com.alquimia.databinding.ItemProfileBinding // Importar o binding do item
import com.bumptech.glide.Glide // Para carregar imagens

class ProfilesAdapter(private val onClick: (UserData) -> Unit) :
    ListAdapter<UserData, ProfilesAdapter.ProfileViewHolder>(ProfileDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val binding = ItemProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProfileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }

    inner class ProfileViewHolder(private val binding: ItemProfileBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onClick(getItem(adapterPosition))
            }
        }

        fun bind(user: UserData) {
            binding.tvProfileName.text = user.name
            binding.tvProfileAgeCity.text = "${user.age} anos, ${user.city}"
            binding.tvProfileInterests.text = "Interesses: ${user.interests?.joinToString(", ") ?: "Nenhum"}"

            // Carregar imagem de perfil com Glide
            user.profilePicture?.let { imageUrl ->
                Glide.with(binding.ivProfilePicture.context)
                    .load(imageUrl)
                    .placeholder(android.R.drawable.sym_def_app_icon) // Placeholder enquanto carrega
                    .error(android.R.drawable.ic_menu_gallery) // Imagem de erro se falhar
                    .into(binding.ivProfilePicture)
            } ?: run {
                binding.ivProfilePicture.setImageResource(android.R.drawable.sym_def_app_icon) // Imagem padrão se não houver URL
            }
        }
    }

    companion object {
        private val ProfileDiffCallback = object : DiffUtil.ItemCallback<UserData>() {
            override fun areItemsTheSame(oldItem: UserData, newItem: UserData): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: UserData, newItem: UserData): Boolean {
                return oldItem == newItem
            }
        }
    }
}
