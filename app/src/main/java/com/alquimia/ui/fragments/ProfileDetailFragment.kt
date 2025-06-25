package com.alquimia.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.alquimia.databinding.FragmentProfileDetailBinding
import com.alquimia.ui.viewmodels.ProfileDetailViewModel
import com.alquimia.util.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileDetailFragment : Fragment() {

    private var _binding: FragmentProfileDetailBinding? = null
    private val binding get() = _binding!!
    private val profileDetailViewModel: ProfileDetailViewModel by viewModels()
    private val args: ProfileDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = args.userId
        profileDetailViewModel.fetchUserProfile(userId)

        setupObservers()
        setupListeners(userId)
    }

    private fun setupObservers() {
        profileDetailViewModel.userProfile.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.tvProfileDetailInfo.text = "Carregando perfil..."
                }
                is Resource.Success -> {
                    resource.data?.let { user ->
                        binding.tvProfileDetailInfo.text = """
                            Nome: ${user.name}
                            Email: ${user.email}
                            Idade: ${user.age}
                            Cidade: ${user.city}
                            Gênero: ${user.gender}
                            Interesses: ${user.interests?.joinToString(", ") ?: "Nenhum"}
                            Foto: ${user.profilePicture ?: "N/A"}
                        """.trimIndent()
                    } ?: run {
                        binding.tvProfileDetailInfo.text = "Erro: Dados do perfil nulos inesperados."
                        Toast.makeText(requireContext(), "Erro: Dados do perfil nulos inesperados.", Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Error -> {
                    binding.tvProfileDetailInfo.text = "Erro ao carregar perfil: ${resource.message}"
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        profileDetailViewModel.conversationState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    Toast.makeText(requireContext(), "Criando/obtendo conversa...", Toast.LENGTH_SHORT).show()
                }
                is Resource.Success -> {
                    resource.data?.let { conversation ->
                        Toast.makeText(requireContext(), "Conversa pronta!", Toast.LENGTH_SHORT).show()
                        val action = ProfileDetailFragmentDirections.actionProfileDetailFragmentToMessagesFragment(
                            conversation.id,
                            conversation.otherUser?.id ?: args.userId
                        )
                        findNavController().navigate(action)
                    } ?: run {
                        Toast.makeText(requireContext(), "Erro: Dados da conversa nulos inesperados.", Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), "Erro ao iniciar conversa: ${resource.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupListeners(otherUserId: String) {
        binding.btnStartChat.setOnClickListener {
            profileDetailViewModel.getOrCreateConversation(otherUserId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
