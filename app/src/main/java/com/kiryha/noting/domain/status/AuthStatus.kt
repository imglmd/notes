package com.kiryha.noting.domain.status

sealed class AuthStatus {
    object Success : AuthStatus()
    sealed class Failure(open val message: String? = null) : AuthStatus() {
        class EmailError(override val message: String?) : Failure(message)
        class UsernameError(override val message: String?) : Failure(message)
        class PasswordError(override val message: String?) : Failure(message)
        class GeneralError(override val message: String?) : Failure(message)
    }
}