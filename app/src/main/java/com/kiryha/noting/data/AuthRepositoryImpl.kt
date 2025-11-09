package com.kiryha.noting.data

import com.kiryha.noting.data.source.network.NetworkDataSource
import com.kiryha.noting.domain.AuthRepository
import com.kiryha.noting.domain.model.User
import com.kiryha.noting.domain.status.AuthStatus
import com.kiryha.noting.domain.status.ResultWithStatus
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
    ): ResultWithStatus<Unit, AuthStatus> {
        return try {

            if (networkSource.isUsernameExists(username)) {
                return ResultWithStatus(
                    Unit,
                    AuthStatus.Failure.UsernameError("This username is already taken.")
                )
            }

            if (networkSource.isEmailExists(email)) {
                return ResultWithStatus(
                    Unit,
                    AuthStatus.Failure.EmailError("This email address is already in use")
                )
            }

            auth.signUpWith(Email) {
                this.email = email
                this.password = password
                data = buildJsonObject {
                    put("username", JsonPrimitive(username))
                }
            }
            ResultWithStatus(Unit, AuthStatus.Success)
        } catch (e: Exception) {
            return ResultWithStatus(
                Unit,
                AuthStatus.Failure.GeneralError("Registration error, try changing your username and/or email address")
            )
        }
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): ResultWithStatus<Unit, AuthStatus> {
        return try {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            ResultWithStatus(Unit, AuthStatus.Success)
        } catch (e: Exception) {
            ResultWithStatus(Unit, AuthStatus.Failure.GeneralError("Incorrect email or password"))
        }
    }

    override suspend fun signOut(): ResultWithStatus<Unit, AuthStatus> {
        return try {
            auth.signOut()
            ResultWithStatus(Unit, AuthStatus.Success)
        } catch (e: Exception) {
            ResultWithStatus(Unit, AuthStatus.Failure.GeneralError(e.toString()))
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

    override suspend fun refreshSession(): ResultWithStatus<Unit, AuthStatus> {
        return try {
            auth.refreshCurrentSession()
            ResultWithStatus(Unit, AuthStatus.Success)
        } catch (e: Exception) {
            ResultWithStatus(Unit, AuthStatus.Failure.GeneralError(e.toString()))
        }
    }
}