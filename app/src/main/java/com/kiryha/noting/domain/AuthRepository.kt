package com.kiryha.noting.domain

import com.kiryha.noting.domain.model.User

interface AuthRepository {
    suspend fun signUpWithEmailAndPassword(
        email: String,
        password: String,
        username: String
    ): Result<Unit>
    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<Unit>
    suspend fun signOut(): Result<Unit>
    suspend fun getCurrentUser(): User?
    suspend fun getCurrentUserId(): String?
    suspend fun isAuthenticated(): Boolean
    suspend fun refreshSession(): Result<Unit>
}
