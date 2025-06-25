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
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginViewModel.loginUser(LoginRequest(email, password))
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnGoogle.setOnClickListener {
            // TODO: Implementar login com Google
            Toast.makeText(this, "Login com Google não implementado ainda", Toast.LENGTH_SHORT).show()
        }

        binding.btnFacebook.setOnClickListener {
            // TODO: Implementar login com Facebook
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
        loginViewModel.loginState.observe(this) { state ->
            when (state) {
                LoginState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnLogin.isEnabled = false
                    binding.btnGoogle.isEnabled = false
                    binding.btnFacebook.isEnabled = false
                    binding.tvForgotPassword.isEnabled = false
                    binding.tvRegister.isEnabled = false
                    binding.cardError.visibility = View.GONE
                }
                is LoginState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    binding.btnGoogle.isEnabled = true
                    binding.btnFacebook.isEnabled = true
                    binding.tvForgotPassword.isEnabled = true
                    binding.tvRegister.isEnabled = true
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                is LoginState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    binding.btnGoogle.isEnabled = true
                    binding.btnFacebook.isEnabled = true
                    binding.tvForgotPassword.isEnabled = true
                    binding.tvRegister.isEnabled = true
                    binding.tvError.text = state.message
                    binding.cardError.visibility = View.VISIBLE
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
