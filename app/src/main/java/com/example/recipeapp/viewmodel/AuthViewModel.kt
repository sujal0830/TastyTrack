package com.example.recipeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthValidationResult {
    object Success : AuthValidationResult()
    data class Error(val messageResId: Int) : AuthValidationResult()
}

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth

    // Form State
    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    // UI State
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _validationError = MutableStateFlow<Int?>(null)
    val validationError: StateFlow<Int?> = _validationError.asStateFlow()
    
    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    // --- Input Handlers ---
    fun onNameChange(newValue: String) {
        _name.value = newValue
        _validationError.value = null
        _authError.value = null
    }

    fun onEmailChange(newValue: String) {
        _email.value = newValue
        _validationError.value = null
        _authError.value = null
    }

    fun onPasswordChange(newValue: String) {
        _password.value = newValue
        _validationError.value = null
        _authError.value = null
    }

    fun onConfirmPasswordChange(newValue: String) {
        _confirmPassword.value = newValue
        _validationError.value = null
        _authError.value = null
    }

    // --- Actions ---

    fun login(onSuccess: () -> Unit) {
        val validation = validateLogin()
        if (validation is AuthValidationResult.Error) {
            _validationError.value = validation.messageResId
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null
            
            auth.signInWithEmailAndPassword(email.value, password.value)
                .addOnCompleteListener { task ->
                    _isLoading.value = false
                    if (task.isSuccessful) {
                        onSuccess()
                    } else {
                        // If "API key not valid" appears here, check google-services.json
                        _authError.value = task.exception?.localizedMessage ?: "Login failed"
                    }
                }
        }
    }

    fun signup(onSuccess: () -> Unit) {
        val validation = validateSignup()
        if (validation is AuthValidationResult.Error) {
            _validationError.value = validation.messageResId
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null
            
            auth.createUserWithEmailAndPassword(email.value, password.value)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Update Display Name
                        val user = auth.currentUser
                        val profileUpdates = userProfileChangeRequest {
                            displayName = name.value
                        }
                        user?.updateProfile(profileUpdates)?.addOnCompleteListener { 
                             _isLoading.value = false
                             onSuccess()
                        } ?: run {
                            _isLoading.value = false
                            onSuccess() 
                        }
                    } else {
                        _isLoading.value = false
                        _authError.value = task.exception?.localizedMessage ?: "Signup failed"
                    }
                }
        }
    }

    private fun validateLogin(): AuthValidationResult {
        if (email.value.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
            return AuthValidationResult.Error(R.string.error_invalid_email)
        }
        if (password.value.length < 6) {
            return AuthValidationResult.Error(R.string.error_password_short)
        }
        return AuthValidationResult.Success
    }

    private fun validateSignup(): AuthValidationResult {
        if (name.value.isBlank()) {
            return AuthValidationResult.Error(R.string.error_empty_name)
        }
        if (email.value.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
            return AuthValidationResult.Error(R.string.error_invalid_email)
        }
        if (password.value.length < 6) {
            return AuthValidationResult.Error(R.string.error_password_short)
        }
        if (password.value != confirmPassword.value) {
            return AuthValidationResult.Error(R.string.error_password_mismatch)
        }
        return AuthValidationResult.Success
    }
}