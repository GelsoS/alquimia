package com.alquimia.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.alquimia.databinding.ActivityForgotPasswordBinding
import com.alquimia.ui.viewmodels.ForgotPasswordViewModel
import com.alquimia.util.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private val forgotPasswordViewModel: ForgotPasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.btnResetPassword.setOnClickListener {
            val email = binding.etEmail.text.toString()
            if (email.isNotEmpty()) {
                forgotPasswordViewModel.resetPassword(email)
            } else {
                Toast.makeText(this, "Por favor, insira seu email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObservers() {
        forgotPasswordViewModel.resetPasswordState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnResetPassword.isEnabled = false
                    binding.cardMessage.visibility = View.GONE
                    binding.cardError.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnResetPassword.isEnabled = true
                    binding.tvMessage.text = resource.data?.message ?: "Link de recuperação enviado (se o email existir)."
                    binding.cardMessage.visibility = View.VISIBLE
                    binding.etEmail.text?.clear() // Limpa o campo de email
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnResetPassword.isEnabled = true
                    binding.tvError.text = resource.message ?: "Erro desconhecido ao redefinir senha."
                    binding.cardError.visibility = View.VISIBLE
                }
            }
        }
    }
}
