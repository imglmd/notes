package com.kiryha.noting.domain

import com.kiryha.noting.domain.model.User
import com.kiryha.noting.domain.status.AuthStatus
import com.kiryha.noting.domain.status.ResultWithStatus

interface AuthRepository {
    suspend fun signUpWithEmailAndPassword(
        email: String,
        password: String,
        username: String
    ): ResultWithStatus<Unit, AuthStatus>
    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): ResultWithStatus<Unit, AuthStatus>
    suspend fun signOut(): ResultWithStatus<Unit, AuthStatus>
    suspend fun getCurrentUser(): User?
    suspend fun getCurrentUserId(): String?
    suspend fun isAuthenticated(): Boolean
    suspend fun refreshSession(): ResultWithStatus<Unit, AuthStatus>
}
