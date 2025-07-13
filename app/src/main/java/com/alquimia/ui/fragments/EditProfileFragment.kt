package com.alquimia.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.alquimia.R // Importar R para acessar recursos
import com.alquimia.databinding.FragmentEditProfileBinding
import com.alquimia.ui.viewmodels.EditProfileViewModel
import com.alquimia.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private val editProfileViewModel: EditProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupGenderSpinner()
        editProfileViewModel.fetchUserProfile()

        setupObservers()
        setupListeners()
    }

    private fun setupGenderSpinner() {
        val genderOptions = resources.getStringArray(R.array.gender_options)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGender.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnSaveProfile.setOnClickListener {
            val name = binding.etName.text.toString()
            val age = binding.etAge.text.toString().toIntOrNull()
            val city = binding.etCity.text.toString()
            val gender = binding.spinnerGender.selectedItem?.toString()

            editProfileViewModel.updateProfile(name, age, city, gender, null)
        }

        binding.btnUploadPicture.setOnClickListener {
            // TODO: Implementar seletor de imagem e passar o File real
            Toast.makeText(requireContext(), "Seletor de imagem não implementado", Toast.LENGTH_SHORT).show()
            // Exemplo de como chamar (você precisaria de um File real)
            // val dummyFile = File(requireContext().cacheDir, "dummy_image.jpg")
            // editProfileViewModel.uploadProfilePicture(dummyFile)
        }
    }

    private fun setupObservers() {
        editProfileViewModel.userProfile.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.tvProfileStatus.text = "Carregando perfil..."
                }
                is Resource.Success -> {
                    resource.data?.let { user ->
                        binding.tvProfileStatus.text = """
                            Nome: ${user.name}
                            Email: ${user.email}
                            Idade: ${user.age}
                            Cidade: ${user.city}
                            Gênero: ${user.gender}
                            Interesses: ${user.interests?.joinToString(", ") ?: "Nenhum"}
                            Foto: ${user.profilePicture ?: "N/A"}
                        """.trimIndent()
                        // Preencher campos de edição
                        binding.etName.setText(user.name)
                        binding.etAge.setText(user.age.toString())
                        binding.etCity.setText(user.city)
                        // Selecionar gênero no spinner
                        val genderOptions = resources.getStringArray(R.array.gender_options)
                        val genderIndex = genderOptions.indexOf(user.gender)
                        if (genderIndex != -1) {
                            binding.spinnerGender.setSelection(genderIndex)
                        }
                    } ?: run {
                        binding.tvProfileStatus.text = "Erro: Dados do perfil nulos inesperados."
                        Toast.makeText(requireContext(), "Erro: Dados do perfil nulos inesperados.", Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Error -> {
                    binding.tvProfileStatus.text = "Erro ao carregar perfil: ${resource.message}"
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        editProfileViewModel.updateProfileState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    Toast.makeText(requireContext(), "Salvando perfil...", Toast.LENGTH_SHORT).show()
                }
                is Resource.Success -> {
                    Toast.makeText(requireContext(), resource.message ?: "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                    editProfileViewModel.fetchUserProfile()
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), "Erro ao atualizar perfil: ${resource.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        editProfileViewModel.uploadPictureState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    Toast.makeText(requireContext(), "Fazendo upload da imagem...", Toast.LENGTH_SHORT).show()
                }
                is Resource.Success -> {
                    Toast.makeText(requireContext(), "Upload de imagem concluído: ${resource.data}", Toast.LENGTH_SHORT).show()
                    editProfileViewModel.fetchUserProfile()
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), "Erro no upload da imagem: ${resource.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
