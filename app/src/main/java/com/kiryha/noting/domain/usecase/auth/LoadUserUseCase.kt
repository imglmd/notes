package com.kiryha.noting.domain.usecase.auth

import com.kiryha.noting.domain.AuthRepository
import com.kiryha.noting.domain.model.User
import com.kiryha.noting.domain.status.AuthStatus
import com.kiryha.noting.domain.status.ResultWithStatus
import kotlinx.coroutines.delay

class LoadUserUseCase(
    private val repository: AuthRepository
){
    suspend operator fun invoke(): ResultWithStatus<User?, AuthStatus> {
        var user: User? = null
        var attempts = 0
        val maxAttempts = 3

        while (user == null && attempts < maxAttempts) {
            user = repository.getCurrentUser()

            if (user == null) {
                attempts++
                if (attempts < maxAttempts) {
                    delay(500)
                }
            }
        }
        return if (user != null) {
            ResultWithStatus(user, AuthStatus.Success)
        } else {
            ResultWithStatus(null, AuthStatus.Failure.GeneralError("Can't load user"))
        }
    }
}