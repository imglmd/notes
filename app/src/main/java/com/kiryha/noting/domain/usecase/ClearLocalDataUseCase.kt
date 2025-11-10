package com.kiryha.noting.domain.usecase

import com.kiryha.noting.domain.NoteRepository
import com.kiryha.noting.domain.model.User
import com.kiryha.noting.domain.status.AuthStatus
import com.kiryha.noting.domain.status.ResultWithStatus

class ClearLocalDataUseCase(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(){
        repository.clearLocalData()
    }
}