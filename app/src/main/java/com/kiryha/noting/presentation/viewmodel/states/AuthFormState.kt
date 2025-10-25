package com.kiryha.noting.presentation.viewmodel.states

data class AuthFormState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val username: String = "",
    val usernameError: String? = null,
    val isLoading: Boolean = false
)
