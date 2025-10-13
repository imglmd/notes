package com.kiryha.noting.domain.status

sealed class AuthStatus {
    object Idle : AuthStatus()
    object Loading : AuthStatus()
    object Success : AuthStatus()
    data class Error(val message: String) : AuthStatus()
}