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
import com.alquimia.ui.main.MainActivity
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
            val confirmPassword = binding.etConfirmPassword.text.toString()
            val name = binding.etName.text.toString()
            val age = binding.etAge.text.toString().toIntOrNull()
            val city = binding.etCity.text.toString()

            val gender = when (binding.rgGender.checkedRadioButtonId) {
                binding.rbMale.id -> "Masculino"
                binding.rbFemale.id -> "Feminino"
                binding.rbNotSpecified.id -> "Não Informado"
                else -> null
            }

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || name.isEmpty() || age == null || city.isEmpty() || gender == null) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                binding.tilConfirmPassword.error = "As senhas não coincidem"
                return@setOnClickListener
            } else {
                binding.tilConfirmPassword.error = null
            }

            val request = RegisterRequest(email, password, name, age, city, gender, emptyList())
            registerViewModel.register(request)
        }

        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun setupObservers() {
        registerViewModel.registerState.observe(this) { state ->
            when (state) {
                RegisterState.Loading -> {
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
                    val intent = Intent(this, MainActivity::class.java)
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
