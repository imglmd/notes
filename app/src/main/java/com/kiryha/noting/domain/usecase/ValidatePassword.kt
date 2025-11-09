package com.kiryha.noting.domain.usecase

import com.kiryha.noting.domain.usecase.ValidationResult

class ValidatePassword {
    fun execute(password: String): ValidationResult {

        if (password.length < 6) {
            return ValidationResult(
                successful = false,
                errorMessage = "Password must be at least 6 characters long"
            )
        }

        val containsLetters = password.any { it.isLetter() }
        val containsDigits = password.any { it.isDigit() }
        if (!containsLetters || !containsDigits) {
            return ValidationResult(
                successful = false,
                errorMessage = "Password must include a letter and a digit."
            )
        }

        return ValidationResult(successful = true)
    }
}