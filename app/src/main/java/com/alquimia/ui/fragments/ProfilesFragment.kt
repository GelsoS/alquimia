package com.alquimia.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.alquimia.databinding.FragmentProfilesBinding
import com.alquimia.ui.viewmodels.ProfilesViewModel
import com.alquimia.util.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfilesFragment : Fragment() {

    private var _binding: FragmentProfilesBinding? = null
    private val binding get() = _binding!!
    private val profilesViewModel: ProfilesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profilesViewModel.fetchAllProfiles()

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        profilesViewModel.profiles.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.tvProfilesStatus.text = "Carregando perfis..."
                }
                is Resource.Success -> {
                    val users = resource.data
                    if (!users.isNullOrEmpty()) {
                        binding.tvProfilesStatus.text = "Perfis: \n" + users.joinToString("\n") { "${it.name} (${it.age})" }
                        // TODO: Configurar um RecyclerView aqui para exibir os perfis
                    } else {
                        binding.tvProfilesStatus.text = "Nenhum perfil encontrado."
                    }
                }
                is Resource.Error -> {
                    binding.tvProfilesStatus.text = "Erro ao carregar perfis: ${resource.message}"
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnViewProfileDetail.setOnClickListener {
            val dummyUserId = "algum_id_de_usuario_aqui" // Substitua por um ID real de um perfil carregado
            val action = ProfilesFragmentDirections.actionProfilesFragmentToProfileDetailFragment(dummyUserId)
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
