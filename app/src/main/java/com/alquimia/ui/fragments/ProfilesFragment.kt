package com.alquimia.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.alquimia.databinding.FragmentProfilesBinding
import com.alquimia.ui.adapters.ProfilesAdapter // Importar o adapter
import com.alquimia.ui.viewmodels.ProfilesViewModel
import com.alquimia.util.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfilesFragment : Fragment() {

    private var _binding: FragmentProfilesBinding? = null
    private val binding get() = _binding!!
    private val profilesViewModel: ProfilesViewModel by viewModels()
    private lateinit var profilesAdapter: ProfilesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profilesAdapter = ProfilesAdapter { user ->
            // Lidar com o clique no perfil
            val action = ProfilesFragmentDirections.actionProfilesFragmentToProfileDetailFragment(user.id)
            findNavController().navigate(action)
        }
        binding.rvProfiles.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = profilesAdapter
        }

        profilesViewModel.fetchAllProfiles()

        setupObservers()
    }

    private fun setupObservers() {
        profilesViewModel.profiles.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // Opcional: mostrar um ProgressBar ou Shimmer
                    // binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    // binding.progressBar.visibility = View.GONE
                    val users = resource.data
                    if (!users.isNullOrEmpty()) {
                        profilesAdapter.submitList(users)
                    } else {
                        profilesAdapter.submitList(emptyList()) // Limpar a lista
                        Toast.makeText(requireContext(), "Nenhum perfil encontrado.", Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Error -> {
                    // binding.progressBar.visibility = View.GONE
                    profilesAdapter.submitList(emptyList()) // Limpar a lista em caso de erro
                    Toast.makeText(requireContext(), "Erro ao carregar perfis: ${resource.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
