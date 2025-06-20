package com.alquimia.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alquimia.data.models.User
import com.alquimia.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfilesViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadUsers() {
        _isLoading.value = true

        viewModelScope.launch {
            val result = userRepository.getAllUsers() // Alterado para getAllUsers

            result.onSuccess { userList ->
                _users.value = userList
            }.onFailure {
                _users.value = emptyList()
                // Opcional: logar o erro ou mostrar uma mensagem para o usuário
            }

            _isLoading.value = false
        }
    }
}
