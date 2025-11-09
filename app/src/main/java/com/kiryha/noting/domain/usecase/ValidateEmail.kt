package com.kiryha.noting.domain.usecase

import android.util.Patterns
import com.kiryha.noting.domain.usecase.ValidationResult

class ValidateEmail {
    fun execute(email: String): ValidationResult {
        val trimmedEmail = email.trim()

        if (trimmedEmail.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = "The email can't be blank"
            )
        }

        if (email.trim().length > 254) {
            return ValidationResult(
                successful = false,
                errorMessage = "The email must not exceed 254 characters"
            )
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Please enter a valid email"
            )
        }

        return ValidationResult(successful = true)
    }
}