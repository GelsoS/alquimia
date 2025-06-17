package com.alquimia.ui.activities

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.alquimia.databinding.ActivityForgotPasswordBinding
import com.alquimia.ui.viewmodels.ForgotPasswordUiState
import com.alquimia.ui.viewmodels.ForgotPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private val viewModel: ForgotPasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.btnSendReset.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            if (validateEmail(email)) {
                viewModel.resetPassword(email)
            }
        }

        binding.btnBackToLogin.setOnClickListener {
            finish()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is ForgotPasswordUiState.Initial -> {
                        showEmailForm()
                        hideLoading()
                    }
                    is ForgotPasswordUiState.Loading -> {
                        showLoading()
                    }
                    is ForgotPasswordUiState.Success -> {
                        hideLoading()
                        showSuccessMessage()
                        viewModel.resetState()
                    }
                    is ForgotPasswordUiState.Error -> {
                        hideLoading()
                        showError(state.message)
                    }
                }
            }
        }
    }

    private fun validateEmail(email: String): Boolean {
        if (email.isEmpty()) {
            binding.tilEmail.error = "Email é obrigatório"
            return false
        }
        binding.tilEmail.error = null
        return true
    }

    private fun showEmailForm() {
        binding.layoutEmailForm.visibility = View.VISIBLE
        binding.layoutSuccess.visibility = View.GONE
        binding.cardError.visibility = View.GONE
    }

    private fun showSuccessMessage() {
        binding.layoutEmailForm.visibility = View.GONE
        binding.layoutSuccess.visibility = View.VISIBLE
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSendReset.isEnabled = false
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.btnSendReset.isEnabled = true
    }

    private fun showError(message: String) {
        binding.tvError.text = message
        binding.cardError.visibility = View.VISIBLE
    }
}
