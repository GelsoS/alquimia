package com.alquimia.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.alquimia.data.remote.models.LoginRequest
import com.alquimia.databinding.ActivityLoginBinding
import com.alquimia.ui.main.MainActivity
import com.alquimia.ui.register.RegisterActivity
import com.alquimia.ui.activities.ForgotPasswordActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.loginUser(LoginRequest(email, password))
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnGoogle.setOnClickListener {
            Toast.makeText(this, "Login com Google não implementado ainda", Toast.LENGTH_SHORT).show()
        }

        binding.btnFacebook.setOnClickListener {
            Toast.makeText(this, "Login com Facebook não implementado ainda", Toast.LENGTH_SHORT).show()
        }

        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    private fun setupObservers() {
        viewModel.loginState.observe(this) { state ->
            when (state) {
                is LoginState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    setButtonsEnabled(false)
                    binding.cardError.visibility = View.GONE
                }
                is LoginState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    setButtonsEnabled(true)
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                is LoginState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    setButtonsEnabled(true)
                    binding.tvError.text = state.message
                    binding.cardError.visibility = View.VISIBLE
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
                else -> {
                    // Este caso não deveria ser alcançado com os estados atuais de LoginState,
                    // mas é adicionado para satisfazer o compilador.
                    // Você pode logar um erro ou lançar uma exceção aqui se quiser.
                    Toast.makeText(this, "Estado de login desconhecido: $state", Toast.LENGTH_LONG).show()
                    binding.progressBar.visibility = View.GONE
                    setButtonsEnabled(true)
                }
            }
        }
    }

    private fun setButtonsEnabled(enabled: Boolean) {
        binding.btnLogin.isEnabled = enabled
        binding.btnGoogle.isEnabled = enabled
        binding.btnFacebook.isEnabled = enabled
        binding.tvForgotPassword.isEnabled = enabled
        binding.tvRegister.isEnabled = enabled
    }
}
