package com.alquimia.ui.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.alquimia.R
import com.alquimia.databinding.FragmentEditProfileBinding
import com.alquimia.ui.viewmodels.EditProfileViewModel
import com.google.android.material.snackbar.Snackbar
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EditProfileViewModel by viewModels()

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            imageUri?.let {
                viewModel.uploadProfilePicture(requireContext(), it)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnUploadPicture.setOnClickListener {
            openImagePicker()
        }

        binding.btnSaveProfile.setOnClickListener {
            saveProfile()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.user.collect { user ->
                user?.let {
                    binding.etName.setText(it.name)
                    binding.etAge.setText(it.age.toString())
                    binding.etCity.setText(it.city)
                    binding.etInterests.setText(it.interests?.joinToString(", ") ?: "") // Exibir interesses
                    when (it.gender) {
                        "Masculino" -> binding.rgGender.check(R.id.rbMale)
                        "Feminino" -> binding.rgGender.check(R.id.rbFemale)
                        "Não Informado" -> binding.rgGender.check(R.id.rbNotInformado)
                        else -> binding.rgGender.clearCheck()
                    }
                    Glide.with(requireContext())
                        .load(it.profile_picture)
                        .placeholder(R.drawable.ic_person)
                        .into(binding.ivProfilePicture)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                binding.btnSaveProfile.isEnabled = !isLoading
            }
        }

        lifecycleScope.launch {
            viewModel.isUploading.collect { isUploading ->
                binding.progressBar.visibility = if (isUploading) View.VISIBLE else View.GONE
                binding.btnUploadPicture.isEnabled = !isUploading
                binding.btnSaveProfile.isEnabled = !isUploading
            }
        }

        lifecycleScope.launch {
            viewModel.uploadSuccess.collect { success ->
                if (success) {
                    Snackbar.make(binding.root, "Foto de perfil atualizada!", Snackbar.LENGTH_SHORT).show()
                    viewModel.resetUploadSuccess()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.saveSuccess.collect { success ->
                if (success) {
                    Snackbar.make(binding.root, "Perfil salvo com sucesso!", Snackbar.LENGTH_SHORT).show()
                    viewModel.resetSaveSuccess()
                    findNavController().navigateUp()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.errorMessage.collect { message ->
                if (message.isNotEmpty()) {
                    showError(message)
                    viewModel.resetErrorMessage()
                } else {
                    hideError()
                }
            }
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        pickImageLauncher.launch(intent)
    }

    private fun saveProfile() {
        val name = binding.etName.text.toString().trim()
        val age = binding.etAge.text.toString().trim().toIntOrNull()
        val city = binding.etCity.text.toString().trim()
        val gender = getSelectedGender()
        val interestsText = binding.etInterests.text.toString().trim()
        val interests = if (interestsText.isNotEmpty()) {
            interestsText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        } else {
            null
        }

        if (validateInput(name, age, city, gender)) {
            viewModel.updateUserProfile(name, age!!, city, gender, interests)
        }
    }

    private fun getSelectedGender(): String {
        return when (binding.rgGender.checkedRadioButtonId) {
            R.id.rbMale -> "Masculino"
            R.id.rbFemale -> "Feminino"
            R.id.rbNotInformado -> "Não Informado"
            else -> ""
        }
    }

    private fun validateInput(name: String, age: Int?, city: String, gender: String): Boolean {
        var isValid = true
        if (name.isEmpty()) {
            binding.tilName.error = "Nome é obrigatório"
            isValid = false
        } else {
            binding.tilName.error = null
        }
        if (age == null || age < 18) {
            binding.tilAge.error = "Idade deve ser um número válido e maior que 18"
            isValid = false
        } else {
            binding.tilAge.error = null
        }
        if (city.isEmpty()) {
            binding.tilCity.error = "Cidade é obrigatória"
            isValid = false
        } else {
            binding.tilCity.error = null
        }
        if (gender.isEmpty()) {
            Snackbar.make(binding.root, "Selecione um gênero", Snackbar.LENGTH_SHORT).show()
            isValid = false
        }
        return isValid
    }

    private fun showError(message: String) {
        binding.tvError.text = message
        binding.cardError.visibility = View.VISIBLE
    }

    private fun hideError() {
        binding.cardError.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
