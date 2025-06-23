package com.alquimia.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.alquimia.R
import com.alquimia.data.models.User
import com.alquimia.databinding.FragmentProfileDetailBinding
import com.alquimia.ui.viewmodels.ProfileDetailViewModel
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileDetailFragment : Fragment() {

    private var _binding: FragmentProfileDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileDetailViewModel by viewModels()
    private val args: ProfileDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        observeViewModel()
        viewModel.loadUserProfile(args.userId) // Carregar perfil do usuário passado via argumento
    }

    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnStartChat.setOnClickListener {
            // Navegar para a tela de mensagens, passando o ID do usuário do perfil
            val action = ProfileDetailFragmentDirections.actionProfileDetailFragmentToMessagesFragment(args.userId)
            findNavController().navigate(action)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.user.collect { user ->
                user?.let {
                    displayUserProfile(it)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.progressBarDetail.visibility = if (isLoading) View.VISIBLE else View.GONE
                binding.btnStartChat.isEnabled = !isLoading
            }
        }

        lifecycleScope.launch {
            viewModel.chatTime.collect { chatTime ->
                binding.tvChatTime.text = "${chatTime} min"
            }
        }

        lifecycleScope.launch {
            viewModel.blurStatus.collect { blurStatus ->
                binding.tvChemistryLevel.text = "🧪 ${blurStatus}%"
            }
        }

        lifecycleScope.launch {
            viewModel.errorMessage.collect { message ->
                if (message.isNotEmpty()) {
                    binding.tvError.text = message
                    binding.cardError.visibility = View.VISIBLE
                } else {
                    binding.cardError.visibility = View.GONE
                }
            }
        }
    }

    private fun displayUserProfile(user: User) {
        binding.tvUserName.text = user.name
        binding.tvUserAgeCity.text = "${user.age} anos • ${user.city}"
        binding.tvUserGender.text = "Gênero: ${user.gender}"

        Glide.with(requireContext())
            .load(user.profile_picture)
            .placeholder(R.drawable.ic_person)
            .into(binding.ivProfilePicture)

        binding.chipGroupInterests.removeAllViews()
        user.interests?.forEach { interest ->
            val chip = Chip(requireContext())
            chip.text = interest
            chip.isClickable = false
            chip.isCheckable = false
            binding.chipGroupInterests.addView(chip)
        }
        if (user.interests.isNullOrEmpty()) {
            val chip = Chip(requireContext())
            chip.text = "Nenhum interesse adicionado"
            chip.isClickable = false
            chip.isCheckable = false
            binding.chipGroupInterests.addView(chip)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
