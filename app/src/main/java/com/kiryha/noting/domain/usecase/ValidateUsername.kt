package com.kiryha.noting.domain.usecase

class ValidateUsername {
    operator fun invoke(name: String): ValidationResult{

        val trimmedName = name.trim()

        if (trimmedName.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Name can't be blank or just spaces."
            )
        }


        if (trimmedName.length < 2) {
            return ValidationResult(
                successful = false,
                errorMessage = "The name must be at least 2 characters long"
            )
        }

        if (trimmedName.length > 50) {
            return ValidationResult(
                successful = false,
                errorMessage = "The name must not exceed 50 characters"
            )
        }

        val invalidSymbolRegex = Regex("[^a-zA-Z\\s0-9]")
        if (invalidSymbolRegex.containsMatchIn(trimmedName)) {
            return ValidationResult(
                successful = false,
                errorMessage = "The name must not contain invalid symbols"
            )
        }

        if (name != trimmedName) {
            return ValidationResult(
                successful = false,
                errorMessage = "The name must not have leading or trailing spaces"
            )
        }

        val forbiddenNames = listOf("admin", "root", "null")
        if (trimmedName.lowercase() in forbiddenNames) {
            return ValidationResult(
                successful = false,
                errorMessage = "The name is not allowed"
            )
        }

        if (trimmedName.any { it.isDigit() }) {
            return ValidationResult(
                successful = false,
                errorMessage = "The name must not contain numbers"
            )
        }

        return ValidationResult(
            successful = true
        )
    }
}