package com.kiryha.noting.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiryha.noting.data.AuthRepository
import com.kiryha.noting.data.NoteRepository
import com.kiryha.noting.domain.status.AuthStatus
import com.kiryha.noting.domain.status.ValidationResult
import com.kiryha.noting.domain.usecase.ValidateEmail
import com.kiryha.noting.domain.usecase.ValidatePassword
import com.kiryha.noting.domain.usecase.ValidateUsername
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword,
    private val validateUsername: ValidateUsername
): ViewModel() {
    private val _authStatus = MutableStateFlow<AuthStatus>(AuthStatus.Idle)
    val authStatus: StateFlow<AuthStatus> = _authStatus

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _validationResult = MutableStateFlow<ValidationResult>(ValidationResult(true))
    val validationResult: StateFlow<ValidationResult> = _validationResult


    fun signUp(email: String, username: String, password: String){
        viewModelScope.launch {
            _authStatus.value = AuthStatus.Loading
        }
    }
    fun signInByEmail(email: String, password: String){
        viewModelScope.launch {
            _authStatus.value = AuthStatus.Loading
            authRepository.signInWithEmailAndPassword(email, password)


        }
    }
    fun signOut(){
        viewModelScope.launch {
            _authStatus.value = AuthStatus.Loading

        }
    }

    private fun submitData() {
        _authStatus.value = AuthStatus.Loading

        val usernameResult = validateUsername.execute(username.value)
        val emailResult = validateEmail.execute(email.value)
        val passwordResult = validatePassword.execute(password.value)

        val hasError = listOf(emailResult, passwordResult).any { !it.successful }

        if (hasError) {
            _authStatus.value = AuthStatus.Error("Input error")
            _validationResult.value = ValidationResult(false, "KQKDQKDNJWENJOO")
            return
        }

    }

    fun onUsernameChange(newText: String){
        _username.value = newText
    }
    fun onEmailChange(newText: String){
        _email.value = newText
    }
    fun onPasswordChange(newText: String){
        _password.value = newText
    }

}