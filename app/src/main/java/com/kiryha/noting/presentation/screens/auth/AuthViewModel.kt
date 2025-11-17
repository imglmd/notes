package com.kiryha.noting.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiryha.noting.domain.AuthRepository
import com.kiryha.noting.domain.model.User
import com.kiryha.noting.domain.status.AuthStatus
import com.kiryha.noting.domain.usecase.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository,
    private val syncNotesUseCase: SyncNotesUseCase,
    private val loadUserUseCase: LoadUserUseCase,
    private val clearLocalDataUseCase: ClearLocalDataUseCase,
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword,
    private val validateUsername: ValidateUsername
) : ViewModel() {

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
            try {
                if (_authState.value != AuthState.Initial) {
                    _authState.value = AuthState.Loading
                }

                val isAuth = repository.isAuthenticated()

                if (isAuth) {
                    loadUserWithRetry()
                } else {
                    _authState.value = AuthState.Unauthenticated
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _authState.value = AuthState.Error(
                    message = "Не удалось проверить статус авторизации"
                )
            }
        }
    }

    private suspend fun loadUserWithRetry() {
        try {
            val result = loadUserUseCase()
            when (result.status) {
                AuthStatus.Success -> {
                    _currentUser.value = result.item
                    _authState.value = AuthState.Authenticated
                }
                is AuthStatus.Failure -> {
                    tryRefreshSession()
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            tryRefreshSession()
        }
    }

    private suspend fun tryRefreshSession() {
        try {
            val refreshResult = repository.refreshSession()
            when (refreshResult.status) {
                AuthStatus.Success -> {
                    val user = repository.getCurrentUser()
                    if (user != null) {
                        _currentUser.value = user
                        _authState.value = AuthState.Authenticated
                    } else {
                        _authState.value = AuthState.Unauthenticated
                    }
                }
                is AuthStatus.Failure -> {
                    _authState.value = AuthState.Unauthenticated
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun signUp() {
        val validationErrors = validateSignUpForm()
        if (validationErrors != null) {
            _formState.value = validationErrors
            return
        }

        viewModelScope.launch {
            try {
                _formState.value = _formState.value.copy(isLoading = true)
                _authState.value = AuthState.Loading

                val result = repository.signUpWithEmailAndPassword(
                    email = _formState.value.email.trim(),
                    password = _formState.value.password,
                    username = _formState.value.username.trim()
                )

                handleAuthResult(result.status, isSignUp = true)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                handleUnexpectedError("Не удалось зарегистрироваться")
            }
        }
    }

    fun signIn() {
        val validationErrors = validateSignInForm()
        if (validationErrors != null) {
            _formState.value = validationErrors
            return
        }

        viewModelScope.launch {
            try {
                _formState.value = _formState.value.copy(isLoading = true)
                _authState.value = AuthState.Loading

                val result = repository.signInWithEmailAndPassword(
                    email = _formState.value.email.trim(),
                    password = _formState.value.password
                )

                when (result.status) {
                    AuthStatus.Success -> {
                        _currentUser.value = repository.getCurrentUser()
                        _authState.value = AuthState.Authenticated
                        _formState.value = AuthFormState()

                        launch {
                            syncNotesUseCase()
                        }
                    }
                    is AuthStatus.Failure -> {
                        handleAuthFailure(result.status)
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                handleUnexpectedError("Не удалось войти")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading

                val result = repository.signOut()

                when (result.status) {
                    AuthStatus.Success -> {
                        clearLocalDataUseCase()
                        _currentUser.value = null
                        _authState.value = AuthState.Unauthenticated
                        _formState.value = AuthFormState()
                    }
                    is AuthStatus.Failure -> {
                        _authState.value = AuthState.Error(
                            result.status.message ?: "Ошибка выхода"
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Не удалось выйти из аккаунта")
            }
        }
    }

    fun clearError() {
        _formState.update { it.copy(
            emailError = null,
            passwordError = null,
            usernameError = null,
        ) }

        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun resetForm() {
        _formState.value = AuthFormState()
    }

    fun onEmailChange(email: String) {
        _formState.update {
            it.copy(
                email = email,
                emailError = null
            )
        }
    }

    fun onPasswordChange(password: String) {
        _formState.update {
            it.copy(
                password = password,
                passwordError = null
            )
        }
    }

    fun onUsernameChange(username: String) {
        _formState.update {
            it.copy(
                username = username,
                usernameError = null
            )
        }
    }


    private fun validateSignUpForm(): AuthFormState? {
        val emailResult = validateEmail(_formState.value.email)
        val passwordResult = validatePassword(_formState.value.password)
        val usernameResult = validateUsername(_formState.value.username)

        val hasError = listOf(
            emailResult,
            passwordResult,
            usernameResult
        ).any { !it.successful }

        return if (hasError) {
            _formState.value.copy(
                emailError = emailResult.errorMessage,
                passwordError = passwordResult.errorMessage,
                usernameError = usernameResult.errorMessage
            )
        } else null
    }

    private fun validateSignInForm(): AuthFormState? {
        val emailResult = validateEmail(_formState.value.email)
        val passwordResult = validatePassword(_formState.value.password)

        val hasError = listOf(emailResult, passwordResult).any { !it.successful }

        return if (hasError) {
            _formState.value.copy(
                emailError = emailResult.errorMessage,
                passwordError = passwordResult.errorMessage
            )
        } else null
    }

    private suspend fun handleAuthResult(status: AuthStatus, isSignUp: Boolean) {
        when (status) {
            AuthStatus.Success -> {
                _currentUser.value = repository.getCurrentUser()
                _authState.value = AuthState.Authenticated
                _formState.value = AuthFormState()
            }
            is AuthStatus.Failure -> {
                handleAuthFailure(status)
            }
        }
    }

    private fun handleAuthFailure(failure: AuthStatus.Failure) {
        val defaultMessage = "Произошла ошибка"

        _formState.update {

            when (failure) {
                is AuthStatus.Failure.EmailError -> {
                    it.copy(
                        emailError = failure.message ?: defaultMessage,
                        isLoading = false
                    )
                }

                is AuthStatus.Failure.UsernameError -> {
                    it.copy(
                        usernameError = failure.message ?: defaultMessage,
                        isLoading = false
                    )
                }

                is AuthStatus.Failure.PasswordError -> {
                    it.copy(
                        passwordError = failure.message ?: defaultMessage,
                        isLoading = false
                    )
                }

                is AuthStatus.Failure.GeneralError -> {
                    it.copy(
                        emailError = failure.message ?: defaultMessage,
                        passwordError = " ",
                        usernameError = " ",
                        isLoading = false
                    )
                }
            }
        }
        _authState.value = AuthState.Unauthenticated
    }

    private fun handleUnexpectedError(message: String) {
        _formState.update {
            it.copy(
            emailError = message,
            passwordError = " ",
            usernameError = " ",
            isLoading = false
        ) }
        _authState.value = AuthState.Error(message)
    }
}