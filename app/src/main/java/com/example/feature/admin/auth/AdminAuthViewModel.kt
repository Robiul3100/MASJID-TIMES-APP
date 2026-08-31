package com.example.feature.admin.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.auth.AdminUser
import com.example.data.firebase.AuthRepository
import com.example.data.firebase.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AdminLoginUiState {
    object Idle : AdminLoginUiState
    object Loading : AdminLoginUiState
    data class Success(val user: AdminUser) : AdminLoginUiState
    data class Error(val message: String) : AdminLoginUiState
}

class AdminAuthViewModel(
    private val authRepository: AuthRepository = AuthRepository.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow<AdminLoginUiState>(AdminLoginUiState.Idle)
    val uiState: StateFlow<AdminLoginUiState> = _uiState.asStateFlow()

    val currentUser: StateFlow<AdminUser?> = authRepository.currentUser

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _isPasswordVisible = MutableStateFlow(false)
    val isPasswordVisible: StateFlow<Boolean> = _isPasswordVisible.asStateFlow()

    fun onEmailChanged(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChanged(newPassword: String) {
        _password.value = newPassword
    }

    fun togglePasswordVisibility() {
        _isPasswordVisible.value = !_isPasswordVisible.value
    }

    fun signIn(onSuccess: (AdminUser) -> Unit) {
        val currentEmail = _email.value.trim()
        val currentPass = _password.value.trim()

        if (currentEmail.isEmpty()) {
            _uiState.value = AdminLoginUiState.Error("দয়া করে ইমেইল অ্যাড্রেস লিখুন")
            return
        }
        if (currentPass.isEmpty()) {
            _uiState.value = AdminLoginUiState.Error("দয়া করে পাসওয়ার্ড লিখুন")
            return
        }

        _uiState.value = AdminLoginUiState.Loading

        viewModelScope.launch {
            when (val result = authRepository.signIn(currentEmail, currentPass)) {
                is AuthResult.Success -> {
                    _uiState.value = AdminLoginUiState.Success(result.user)
                    onSuccess(result.user)
                }
                is AuthResult.Error -> {
                    _uiState.value = AdminLoginUiState.Error(result.message)
                }
                is AuthResult.Loading -> {
                    _uiState.value = AdminLoginUiState.Loading
                }
            }
        }
    }

    fun resetError() {
        _uiState.value = AdminLoginUiState.Idle
    }

    fun signOut() {
        authRepository.signOut()
        _uiState.value = AdminLoginUiState.Idle
        _email.value = ""
        _password.value = ""
    }
}
