package com.alquimia.ui.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.alquimia.data.remote.models.RegisterRequest
import com.alquimia.databinding.ActivityRegisterBinding
import com.alquimia.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            performRegistration()
        }

        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun performRegistration() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()
        val name = binding.etName.text.toString().trim()
        val ageText = binding.etAge.text.toString().trim()
        val city = binding.etCity.text.toString().trim()

        // Validações
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
            name.isEmpty() || ageText.isEmpty() || city.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        val age = ageText.toIntOrNull()
        if (age == null || age < 18) {
            Toast.makeText(this, "Idade deve ser um número válido e maior que 18", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            binding.tilConfirmPassword.error = "As senhas não coincidem"
            return
        } else {
            binding.tilConfirmPassword.error = null
        }

        val gender = when (binding.rgGender.checkedRadioButtonId) {
            binding.rbMale.id -> "Masculino"
            binding.rbFemale.id -> "Feminino"
            binding.rbNotSpecified.id -> "Não Informado"
            else -> {
                Toast.makeText(this, "Selecione um gênero", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val request = RegisterRequest(email, password, name, age, city, gender, emptyList())
        viewModel.register(request)
    }

    private fun setupObservers() {
        viewModel.registerState.observe(this) { state ->
            when (state) {
                is RegisterState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnRegister.isEnabled = false
                    binding.tvLogin.isEnabled = false
                    binding.cardError.visibility = View.GONE
                }
                is RegisterState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                    binding.tvLogin.isEnabled = true
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, com.alquimia.ui.main.MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                is RegisterState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                    binding.tvLogin.isEnabled = true
                    binding.tvError.text = state.message
                    binding.cardError.visibility = View.VISIBLE
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
