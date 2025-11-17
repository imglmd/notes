package com.kiryha.noting.domain.usecase.notes

import com.kiryha.noting.domain.NoteRepository
import com.kiryha.noting.domain.status.NoteStatus
import com.kiryha.noting.domain.status.ResultWithStatus

class SyncNotesUseCase(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(): ResultWithStatus<Unit, NoteStatus> {
        return noteRepository.fullSync()
    }
}