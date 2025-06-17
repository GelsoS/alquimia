package com.alquimia.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alquimia.data.models.User
import com.alquimia.databinding.ItemProfileCardBinding
import com.bumptech.glide.Glide

class ProfilesAdapter(
    private val onProfileClick: (String) -> Unit
) : ListAdapter<User, ProfilesAdapter.ProfileViewHolder>(ProfileDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val binding = ItemProfileCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProfileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProfileViewHolder(
        private val binding: ItemProfileCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.tvName.text = user.name
            binding.tvAgeCity.text = "${user.age} anos • ${user.city}"
            binding.tvChemistry.text = "🧪 ${(0..100).random()}%"

            Glide.with(binding.root.context)
                .load(user.profile_picture)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(binding.ivProfile)

            binding.root.setOnClickListener {
                onProfileClick(user.id)
            }
        }
    }

    class ProfileDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}
