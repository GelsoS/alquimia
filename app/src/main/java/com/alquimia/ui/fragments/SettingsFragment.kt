package com.alquimia.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.alquimia.databinding.FragmentSettingsBinding
import com.alquimia.ui.login.LoginActivity
import com.alquimia.ui.viewmodels.SettingsViewModel
import com.alquimia.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import com.bumptech.glide.Glide
import androidx.navigation.fragment.findNavController

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsViewModel.fetchUserProfile()

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        settingsViewModel.userProfile.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.tvProfileInfo.text = "Carregando configurações..."
                }
                is Resource.Success -> {
                    resource.data?.let { user ->
                        binding.tvProfileInfo.text = """
                            Nome: ${user.name}
                            Email: ${user.email}
                            Idade: ${user.age}
                            Cidade: ${user.city}
                            Gênero: ${user.gender}
                        """.trimIndent() // Removido a linha da foto daqui

                        // Carregar imagem de perfil com Glide
                        user.profilePicture?.let { imageUrl ->
                            Glide.with(binding.ivSettingsProfilePicture.context)
                                .load(imageUrl)
                                .placeholder(android.R.drawable.sym_def_app_icon)
                                .error(android.R.drawable.ic_menu_gallery)
                                .into(binding.ivSettingsProfilePicture)
                        } ?: run {
                            binding.ivSettingsProfilePicture.setImageResource(android.R.drawable.sym_def_app_icon)
                        }
                    } ?: run {
                        binding.tvProfileInfo.text = "Erro: Dados do perfil nulos inesperados."
                        Toast.makeText(requireContext(), "Erro: Dados do perfil nulos inesperados.", Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Error -> {
                    binding.tvProfileInfo.text = "Erro ao carregar configurações: ${resource.message}"
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnLogout.setOnClickListener {
            settingsViewModel.logout()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }

        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToEditProfileFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
