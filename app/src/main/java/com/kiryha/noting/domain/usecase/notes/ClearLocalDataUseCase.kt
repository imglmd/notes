package com.kiryha.noting.domain.usecase.notes

import com.kiryha.noting.domain.NoteRepository

class ClearLocalDataUseCase(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(){
        repository.clearLocalData()
    }
}