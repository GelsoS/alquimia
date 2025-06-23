package com.alquimia.ui.register

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.alquimia.data.remote.models.RegisterRequest
import com.alquimia.databinding.ActivityRegisterBinding
import com.alquimia.ui.login.LoginActivity
import com.alquimia.ui.main.MainActivity // Assumindo que MainActivity é a tela principal
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val name = binding.etName.text.toString()
            val age = binding.etAge.text.toString().toIntOrNull()
            val city = binding.etCity.text.toString()
            val gender = binding.spinnerGender.selectedItem?.toString() // Assumindo um Spinner para gênero

            if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty() && age != null && city.isNotEmpty() && gender != null) {
                val request = RegisterRequest(email, password, name, age, city, gender, emptyList()) // Interesses vazios por enquanto
                registerViewModel.register(request)
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvLoginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun setupObservers() {
        registerViewModel.registerState.observe(this) { state ->
            when (state) {
                RegisterState.Loading -> {
                    // Mostrar progresso
                    binding.btnRegister.isEnabled = false
                }
                is RegisterState.Success -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    binding.btnRegister.isEnabled = true
                    // Redirecionar para a tela principal após o registro
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                is RegisterState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                    binding.btnRegister.isEnabled = true
                }
            }
        }
    }
}
