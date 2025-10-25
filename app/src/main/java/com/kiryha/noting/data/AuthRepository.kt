package com.kiryha.noting.data

import com.kiryha.noting.data.source.network.NetworkDataSource
import com.kiryha.noting.domain.model.User
import com.kiryha.noting.domain.status.NoteStatus
import com.kiryha.noting.domain.status.ResultWithStatus
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

class AuthRepository(
    private val networkSource: NetworkDataSource
) {
    private val auth = networkSource.auth

    suspend fun signUpWithEmailAndPassword(
        email: String,
        password: String,
        username: String
    ): Result<Unit> {
        return try {
            if (networkSource.isEmailExists(email)) {
                return Result.failure(Exception("Email уже используется"))
            }

            if (networkSource.isUsernameExists(username)) {
                return Result.failure(Exception("Имя пользователя уже занято"))
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
            Result.failure(e)
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
            Result.failure(Exception("Неверный email или пароль"))
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

    fun isAuthenticated(): Boolean {
        return auth.currentSessionOrNull() != null
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
