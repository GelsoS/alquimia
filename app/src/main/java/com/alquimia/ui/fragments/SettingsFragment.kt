package com.alquimia.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.alquimia.R
import com.alquimia.data.models.User
import com.alquimia.databinding.FragmentSettingsBinding
import com.alquimia.ui.activities.LoginActivity
import com.alquimia.ui.viewmodels.LogoutUiState
import com.alquimia.ui.viewmodels.SettingsViewModel
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        observeViewModel()
        viewModel.loadCurrentUserProfile() // Carregar perfil ao criar a view
    }

    private fun setupUI() {
        binding.btnLogout.setOnClickListener {
            viewModel.signOut()
        }
        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_editProfileFragment)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.logoutState.collect { state ->
                when (state) {
                    is LogoutUiState.Initial -> {
                        binding.progressBar.visibility = View.GONE
                        binding.tvLogoutMessage.visibility = View.GONE
                    }
                    is LogoutUiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.tvLogoutMessage.visibility = View.GONE
                    }
                    is LogoutUiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val intent = Intent(requireActivity(), LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        requireActivity().finish()
                        viewModel.resetLogoutState()
                    }
                    is LogoutUiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.tvLogoutMessage.text = state.message
                        binding.tvLogoutMessage.visibility = View.VISIBLE
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.user.collect { user ->
                user?.let {
                    displayUserProfile(it)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isLoadingProfile.collect { isLoading ->
                binding.progressBarProfile.visibility = if (isLoading) View.VISIBLE else View.GONE
                binding.btnEditProfile.isEnabled = !isLoading
                binding.btnLogout.isEnabled = !isLoading
            }
        }

        lifecycleScope.launch {
            viewModel.errorMessage.collect { message ->
                if (message.isNotEmpty()) {
                    binding.tvLogoutMessage.text = message // Reutilizando para erros de carregamento de perfil
                    binding.tvLogoutMessage.visibility = View.VISIBLE
                } else {
                    binding.tvLogoutMessage.visibility = View.GONE
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
