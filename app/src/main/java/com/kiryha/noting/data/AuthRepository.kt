package com.kiryha.noting.data

import com.kiryha.noting.data.source.network.NetworkDataSource
import com.kiryha.noting.domain.model.User
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

class AuthRepository(
    private val networkSource: NetworkDataSource
) {
    private val auth = networkSource.auth


    val sessionStatus: Flow<Boolean> = auth.sessionStatus.map { status ->
        status is SessionStatus.Authenticated
    }

    suspend fun signUpWithEmailAndPassword(
        email: String,
        password: String,
        username: String
    ): Result<Unit> {
        return try {
            if (networkSource.isEmailExists(email)) {
                return Result.failure(Exception("This email address is already in use"))
            }

            if (networkSource.isUsernameExists(username)) {
                return Result.failure(Exception("This username is already taken."))
            }

            auth.signUpWith(Email) {
                this.email = email
                this.password = password
                data = buildJsonObject {
                    put("username", JsonPrimitive(username))
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Registration error, try changing your username and/or email address"))
        }
    }

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<Unit> {
        return try {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Incorrect email or password"))
        }
    }

    suspend fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCurrentUser(): User? {
        return networkSource.getCurrentUser()?.toExternal()
    }

    suspend fun getCurrentUserId(): String? {
        return auth.currentUserOrNull()?.id
    }


    suspend fun isAuthenticated(): Boolean {
        val status = auth.sessionStatus.first { it !is SessionStatus.Initializing }
        return status is SessionStatus.Authenticated
    }

    suspend fun refreshSession(): Result<Unit> {
        return try {
            auth.refreshCurrentSession()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}