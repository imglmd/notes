package com.kiryha.noting.presentation.screens.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiryha.noting.domain.AuthRepository
import com.kiryha.noting.domain.NoteRepository
import com.kiryha.noting.domain.model.User
import com.kiryha.noting.domain.status.AuthStatus
import com.kiryha.noting.domain.usecase.ValidateEmail
import com.kiryha.noting.domain.usecase.ValidatePassword
import com.kiryha.noting.domain.usecase.ValidateUsername
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val noteRepository: NoteRepository,
) : ViewModel() {
    private val validateEmail = ValidateEmail()
    private val validatePassword = ValidatePassword()
    private val validateUsername = ValidateUsername()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _formState = MutableStateFlow(AuthFormState())
    val formState: StateFlow<AuthFormState> = _formState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            if (_authState.value != AuthState.Initial) {
                _authState.value = AuthState.Loading
            }

            try {
                val isAuth = authRepository.isAuthenticated()
                Log.d("AuthViewModel", "isAuthenticated: $isAuth")

                if (isAuth) {
                    loadUserWithRetry()
                } else {
                    Log.d("AuthViewModel", "Сессия не найдена")
                    _authState.value = AuthState.Unauthenticated
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Ошибка при проверке статуса", e)
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    private suspend fun loadUserWithRetry() {
        var user: User? = null
        var attempts = 0
        val maxAttempts = 3

        while (user == null && attempts < maxAttempts) {
            Log.d("AuthViewModel", "Попытка загрузки пользователя: ${attempts + 1}")
            user = authRepository.getCurrentUser()

            if (user == null) {
                attempts++
                if (attempts < maxAttempts) {
                    delay(500)
                }
            }
        }

        if (user != null) {
            Log.d("AuthViewModel", "Пользователь загружен: ${user.username}, ${user.email}")
            _currentUser.value = user
            _authState.value = AuthState.Authenticated
        } else {
            Log.e("AuthViewModel", "Не удалось загрузить пользователя после $maxAttempts попыток")
            tryRefreshSession()
        }
    }

    private suspend fun tryRefreshSession() {
        val refreshResult = authRepository.refreshSession()
        if (refreshResult.status == AuthStatus.Success) {
            val user = authRepository.getCurrentUser()
            if (user != null) {
                _currentUser.value = user
                _authState.value = AuthState.Authenticated
            } else {
                _authState.value = AuthState.Unauthenticated
            }
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun signUp() {
        val emailResult = validateEmail.execute(_formState.value.email)
        val passwordResult = validatePassword.execute(_formState.value.password)
        val usernameResult = validateUsername.execute(_formState.value.username)

        val hasError = listOf(
            emailResult,
            passwordResult,
            usernameResult
        ).any { !it.successful }

        if (hasError) {
            _formState.value = _formState.value.copy(
                emailError = emailResult.errorMessage,
                passwordError = passwordResult.errorMessage,
                usernameError = usernameResult.errorMessage
            )
            return
        }

        viewModelScope.launch {
            _formState.value = _formState.value.copy(isLoading = true)
            _authState.value = AuthState.Loading

            val result = authRepository.signUpWithEmailAndPassword(
                email = _formState.value.email.trim(),
                password = _formState.value.password,
                username = _formState.value.username.trim()
            )

            when (val status = result.status) {
                AuthStatus.Success -> {
                    _currentUser.value = authRepository.getCurrentUser()
                    _authState.value = AuthState.Authenticated
                    _formState.value = AuthFormState()
                }
                is AuthStatus.Failure.EmailError -> {
                    _formState.value = _formState.value.copy(
                        emailError = status.message,
                        isLoading = false
                    )
                    _authState.value = AuthState.Unauthenticated
                }
                is AuthStatus.Failure.UsernameError -> {
                    _formState.value = _formState.value.copy(
                        usernameError = status.message,
                        isLoading = false
                    )
                    _authState.value = AuthState.Unauthenticated
                }
                is AuthStatus.Failure.PasswordError -> {
                    _formState.value = _formState.value.copy(
                        passwordError = status.message,
                        isLoading = false
                    )
                    _authState.value = AuthState.Unauthenticated
                }
                is AuthStatus.Failure.GeneralError -> {
                    _formState.value = _formState.value.copy(
                        emailError = status.message,
                        passwordError = " ",
                        usernameError = " ",
                        isLoading = false
                    )
                    _authState.value = AuthState.Unauthenticated
                }
            }
        }
    }

    fun signIn() {
        val emailResult = validateEmail.execute(_formState.value.email)
        val passwordResult = validatePassword.execute(_formState.value.password)

        val hasError = listOf(emailResult, passwordResult).any { !it.successful }

        if (hasError) {
            _formState.value = _formState.value.copy(
                emailError = emailResult.errorMessage,
                passwordError = passwordResult.errorMessage
            )
            return
        }

        viewModelScope.launch {
            _formState.value = _formState.value.copy(isLoading = true)
            _authState.value = AuthState.Loading

            val result = authRepository.signInWithEmailAndPassword(
                email = _formState.value.email.trim(),
                password = _formState.value.password
            )

            when (val status = result.status) {
                AuthStatus.Success -> {
                    _currentUser.value = authRepository.getCurrentUser()
                    _authState.value = AuthState.Authenticated
                    _formState.value = AuthFormState()

                    // Синхронизация заметок после входа
                    noteRepository.fullSync()
                }
                is AuthStatus.Failure -> {
                    // При ошибке входа показываем ошибку на обоих полях
                    _formState.value = _formState.value.copy(
                        emailError = status.message ?: "Ошибка входа",
                        passwordError = " ",
                        isLoading = false
                    )
                    _authState.value = AuthState.Unauthenticated
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = authRepository.signOut()

            when (result.status) {
                AuthStatus.Success -> {
                    noteRepository.clearLocalData()
                    _currentUser.value = null
                    _authState.value = AuthState.Unauthenticated
                    _formState.value = AuthFormState()
                }
                is AuthStatus.Failure -> {
                    _authState.value = AuthState.Error(
                        (result.status as AuthStatus.Failure).message ?: "Ошибка выхода"
                    )
                }
            }
        }
    }


    fun clearError() {
        if (_authState.value is AuthState.Error) {
            checkAuthStatus()
        }
    }

    fun resetForm() {
        _formState.value = AuthFormState()
    }

    fun onEmailChange(email: String) {
        _formState.value = _formState.value.copy(
            email = email,
            emailError = null
        )
    }

    fun onPasswordChange(password: String) {
        _formState.value = _formState.value.copy(
            password = password,
            passwordError = null
        )
    }

    fun onUsernameChange(username: String) {
        _formState.value = _formState.value.copy(
            username = username,
            usernameError = null
        )
    }
}