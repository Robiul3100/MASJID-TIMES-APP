package com.robiul.mosquetime.feature.admin.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robiul.mosquetime.core.auth.AdminRole
import com.robiul.mosquetime.core.auth.AdminUser
import com.robiul.mosquetime.data.firebase.AuthRepository
import com.robiul.mosquetime.data.firebase.AuthResult
import com.robiul.mosquetime.data.firebase.MosqueConfig
import com.robiul.mosquetime.data.firebase.MosqueConfigManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AdminLoginUiState {
    object Idle : AdminLoginUiState
    object Loading : AdminLoginUiState
    data class Success(val user: AdminUser) : AdminLoginUiState
    data class Error(val message: String) : AdminLoginUiState
}

@HiltViewModel
class AdminAuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val mosqueConfigManager: MosqueConfigManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<AdminLoginUiState>(AdminLoginUiState.Idle)
    val uiState: StateFlow<AdminLoginUiState> = _uiState.asStateFlow()

    val currentUser: StateFlow<AdminUser?> = authRepository.currentUser
    val allAdmins: StateFlow<List<AdminUser>> = authRepository.allAdmins

    val configuredMosques: StateFlow<List<MosqueConfig>> = mosqueConfigManager.configuredMosques
    val activeMosque: StateFlow<MosqueConfig> = mosqueConfigManager.activeMosque

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

    fun sendPasswordReset(emailToReset: String, onResult: (Boolean, String) -> Unit) {
        val targetEmail = emailToReset.trim().ifEmpty { _email.value.trim() }
        if (targetEmail.isEmpty()) {
            onResult(false, "দয়া করে আপনার ইমেইল অ্যাড্রেস দিন")
            return
        }
        viewModelScope.launch {
            val result = authRepository.sendPasswordResetEmail(targetEmail)
            if (result.isSuccess) {
                onResult(true, "পাসওয়ার্ড রিসেট লিংক আপনার ইমেইলে পাঠানো হয়েছে। ইনবক্স বা স্প্যাম ফোল্ডার চেক করুন।")
            } else {
                onResult(false, result.exceptionOrNull()?.message ?: "পাসওয়ার্ড রিসেট ইমেইল পাঠানো সম্ভব হয়নি")
            }
        }
    }

    fun createOrUpdateAdmin(admin: AdminUser, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val res = authRepository.createOrUpdateAdmin(admin)
            if (res.isSuccess) {
                onComplete(true, "অ্যাডমিন তথ্য সফলভাবে সংরক্ষিত হয়েছে")
            } else {
                onComplete(false, "অ্যাডমিন সংরক্ষণ করতে ব্যর্থ হয়েছে")
            }
        }
    }

    fun deleteAdmin(adminUid: String, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val res = authRepository.deleteAdmin(adminUid)
            if (res.isSuccess) {
                onComplete(true, "অ্যাডমিন সফলভাবে মুছে ফেলা হয়েছে")
            } else {
                onComplete(false, res.exceptionOrNull()?.message ?: "অ্যাডমিন মুছে ফেলা সম্ভব হয়নি")
            }
        }
    }

    fun updateAdminRole(adminUid: String, newRole: AdminRole, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val res = authRepository.updateAdminRole(adminUid, newRole)
            if (res.isSuccess) {
                onComplete(true, "অ্যাডমিন রোল পরিবর্তন করা হয়েছে")
            } else {
                onComplete(false, res.exceptionOrNull()?.message ?: "রোল পরিবর্তন করা সম্ভব হয়নি")
            }
        }
    }

    fun toggleAdminStatus(adminUid: String, isActive: Boolean, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val res = authRepository.toggleAdminStatus(adminUid, isActive)
            if (res.isSuccess) {
                onComplete(true, if (isActive) "অ্যাকাউন্ট সক্রিয় করা হয়েছে" else "অ্যাকাউন্ট নিষ্ক্রিয় করা হয়েছে")
            } else {
                onComplete(false, res.exceptionOrNull()?.message ?: "স্ট্যাটাস পরিবর্তন করা সম্ভব হয়নি")
            }
        }
    }

    fun switchMosque(mosqueId: String, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val res = mosqueConfigManager.switchActiveMosque(mosqueId)
            if (res.isSuccess) {
                onComplete(true, "মসজিদ ডাটাবেস পরিবর্তিত হয়েছে: ${res.getOrNull()?.nameBn}")
            } else {
                onComplete(false, res.exceptionOrNull()?.message ?: "মসজিদ পরিবর্তন ব্যর্থ হয়েছে")
            }
        }
    }

    fun registerNewMosque(config: MosqueConfig, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val res = mosqueConfigManager.registerOrUpdateMosque(config)
            if (res.isSuccess) {
                onComplete(true, "নতুন মসজিদ ডাটাবেস সফলভাবে তৈরি হয়েছে")
            } else {
                onComplete(false, "মসজিদ তৈরি করতে সমস্যা হয়েছে")
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
