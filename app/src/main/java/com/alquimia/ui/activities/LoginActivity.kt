package com.alquimia.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.alquimia.databinding.ActivityLoginBinding
import com.alquimia.ui.viewmodels.LoginUiState
import com.alquimia.ui.viewmodels.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Receber dados de registro se vierem do RegisterActivity
        intent.getStringExtra("registered_email")?.let { email ->
            binding.etEmail.setText(email)
            val name = intent.getStringExtra("registered_name")
            val age = intent.getIntExtra("registered_age", 0).takeIf { it != 0 }
            val city = intent.getStringExtra("registered_city")
            val gender = intent.getStringExtra("registered_gender")
            viewModel.initRegistrationData(name, age, city, gender)
            Snackbar.make(binding.root, "Confirme seu email e faça login.", Snackbar.LENGTH_LONG).show()
        }

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInput(email, password)) {
                viewModel.signIn(email, password)
            }
        }

        binding.btnGoogle.setOnClickListener {
            viewModel.signInWithGoogle(this)
        }

        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is LoginUiState.Initial -> {
                        hideLoading()
                        hideError()
                    }
                    is LoginUiState.Loading -> {
                        showLoading()
                        hideError()
                    }
                    is LoginUiState.Success -> {
                        hideLoading()
                        navigateToMain()
                        viewModel.resetState()
                    }
                    is LoginUiState.Error -> {
                        hideLoading()
                        showError(state.message)
                    }
                }
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding.tilEmail.error = "Email é obrigatório"
            return false
        }
        if (password.isEmpty()) {
            binding.tilPassword.error = "Senha é obrigatória"
            return false
        }
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        return true
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnLogin.isEnabled = false
        binding.btnGoogle.isEnabled = false
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.btnLogin.isEnabled = true
        binding.btnGoogle.isEnabled = true
    }

    private fun showError(message: String) {
        binding.tvError.text = message
        binding.cardError.visibility = View.VISIBLE
    }

    private fun hideError() {
        binding.cardError.visibility = View.GONE
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
