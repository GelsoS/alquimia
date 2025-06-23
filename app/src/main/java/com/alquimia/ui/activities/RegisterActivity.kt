package com.alquimia.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.alquimia.databinding.ActivityRegisterBinding
import com.alquimia.ui.viewmodels.RegisterUiState
import com.alquimia.ui.viewmodels.RegisterViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()
            val age = binding.etAge.text.toString().trim()
            val city = binding.etCity.text.toString().trim()
            val gender = getSelectedGender()
            // Interesses não são coletados na tela de registro atual, mas podem ser adicionados
            val interests: List<String>? = null // Ou coletar de um campo se você adicionar um

            if (validateInput(name, email, password, confirmPassword, age, city, gender)) {
                viewModel.signUp(
                    email = email,
                    password = password,
                    name = name,
                    age = age.toInt(),
                    city = city,
                    gender = gender,
                    interests = interests // Passar interesses
                )
            }
        }

        binding.tvLogin.setOnClickListener {
            finish() // Volta para LoginActivity
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is RegisterUiState.Initial -> {
                        hideLoading()
                        hideError()
                    }
                    is RegisterUiState.Loading -> {
                        showLoading()
                        hideError()
                    }
                    is RegisterUiState.Success -> {
                        hideLoading()
                        showSuccessAndNavigateToLogin()
                        viewModel.resetState()
                    }
                    is RegisterUiState.Error -> {
                        hideLoading()
                        showError(state.message)
                    }
                }
            }
        }
    }

    private fun getSelectedGender(): String {
        return when (binding.rgGender.checkedRadioButtonId) {
            binding.rbMale.id -> "Masculino"
            binding.rbFemale.id -> "Feminino"
            else -> ""
        }
    }

    private fun validateInput(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        age: String,
        city: String,
        gender: String
    ): Boolean {
        var isValid = true

        if (name.isEmpty()) {
            binding.tilName.error = "Nome é obrigatório"
            isValid = false
        } else {
            binding.tilName.error = null
        }

        if (email.isEmpty()) {
            binding.tilEmail.error = "Email é obrigatório"
            isValid = false
        } else {
            binding.tilEmail.error = null
        }

        if (password.length < 6) {
            binding.tilPassword.error = "Senha deve ter pelo menos 6 caracteres"
            isValid = false
        } else {
            binding.tilPassword.error = null
        }

        if (password != confirmPassword) {
            binding.tilConfirmPassword.error = "Senhas não coincidem"
            isValid = false
        } else {
            binding.tilConfirmPassword.error = null
        }

        if (age.isEmpty() || age.toIntOrNull() == null || age.toInt() < 18) {
            binding.tilAge.error = "Idade deve ser maior que 18 anos"
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

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnRegister.isEnabled = false
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.btnRegister.isEnabled = true
    }

    private fun showError(message: String) {
        binding.tvError.text = message
        binding.cardError.visibility = View.VISIBLE
    }

    private fun hideError() {
        binding.cardError.visibility = View.GONE
    }

    private fun showSuccessAndNavigateToLogin() {
        Snackbar.make(
            binding.root,
            "Conta criada! Faça login.", // Mensagem ajustada
            Snackbar.LENGTH_LONG
        ).show()

        binding.root.postDelayed({
            val intent = Intent(this, LoginActivity::class.java).apply {
                putExtra("registered_email", binding.etEmail.text.toString().trim())
                // Remover outros extras, pois o perfil será criado no backend
            }
            startActivity(intent)
            finish()
        }, 2000)
    }
}
