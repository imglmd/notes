package com.kiryha.noting.data

import com.kiryha.noting.data.source.network.NetworkDataSource
import com.kiryha.noting.domain.AuthRepository
import com.kiryha.noting.domain.model.User
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

class AuthRepositoryImpl(
    private val networkSource: NetworkDataSource
): AuthRepository {
    private val auth = networkSource.auth


    val sessionStatus: Flow<Boolean> = auth.sessionStatus.map { status ->
        status is SessionStatus.Authenticated
    }

    override suspend fun signUpWithEmailAndPassword(
        email: String,
        password: String,
        username: String
    ): Result<Unit> {
        return try {

            if (networkSource.isUsernameExists(username)) {
                return Result.failure(Exception("This username is already taken."))
            }

            if (networkSource.isEmailExists(email)) {
                return Result.failure(Exception("This email address is already in use"))
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

    override suspend fun signInWithEmailAndPassword(
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

    override suspend fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): User? {
        return networkSource.getCurrentUser()?.toExternal()
    }

    override suspend fun getCurrentUserId(): String? {
        return auth.currentUserOrNull()?.id
    }


    override suspend fun isAuthenticated(): Boolean {
        val status = auth.sessionStatus.first { it !is SessionStatus.Initializing }
        return status is SessionStatus.Authenticated
    }

    override suspend fun refreshSession(): Result<Unit> {
        return try {
            auth.refreshCurrentSession()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}