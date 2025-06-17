package com.alquimia.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.alquimia.databinding.FragmentSettingsBinding
import com.alquimia.ui.activities.LoginActivity
import com.alquimia.ui.viewmodels.LogoutUiState
import com.alquimia.ui.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.btnLogout.setOnClickListener {
            viewModel.signOut()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.logoutState.collect { state ->
                when (state) {
                    is LogoutUiState.Initial -> {
                        binding.progressBar.visibility = View.GONE
                        binding.tvLogoutMessage.visibility = View.GONE
                    }
                    is LogoutUiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.tvLogoutMessage.visibility = View.GONE
                    }
                    is LogoutUiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        // Navegar para a tela de login após o logout
                        val intent = Intent(requireActivity(), LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        requireActivity().finish()
                        viewModel.resetLogoutState()
                    }
                    is LogoutUiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.tvLogoutMessage.text = state.message
                        binding.tvLogoutMessage.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
