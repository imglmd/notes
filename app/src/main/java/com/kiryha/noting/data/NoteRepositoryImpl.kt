package com.kiryha.noting.data

import android.content.Context
import com.kiryha.noting.data.source.local.DeletedNote
import com.kiryha.noting.data.source.local.DeletedNoteDao
import com.kiryha.noting.domain.status.NoteStatus
import com.kiryha.noting.domain.status.ResultWithStatus
import com.kiryha.noting.data.source.local.NoteDao
import com.kiryha.noting.data.source.network.NetworkDataSource
import com.kiryha.noting.domain.AuthRepository
import com.kiryha.noting.domain.NoteRepository
import com.kiryha.noting.domain.model.Note
import com.kiryha.noting.utils.NetworkChecker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.math.absoluteValue

class NoteRepositoryImpl(
    private val noteDao: NoteDao,
    private val deletedNoteDao: DeletedNoteDao,
    private val networkSource: NetworkDataSource,
    private val authRepository: AuthRepository,
    private val context: Context
): NoteRepository {

    private val networkChecker = NetworkChecker(context)
    override suspend fun upsertNote(note: Note): ResultWithStatus<List<Note>> {
        return withContext(Dispatchers.IO) {
            try {
                val userId = authRepository.getCurrentUserId()

                val noteWithId = if (note.id == 0) {
                    note.copy(id = UUID.randomUUID().hashCode().absoluteValue)
                } else {
                    note
                }

                noteDao.upsertNote(noteWithId.toLocal(userId).copy(isSynced = false))

                if (networkChecker.isOnline() && userId != null) {
                    try {
                        networkSource.upsertNote(noteWithId.toNetwork(userId))
                        noteDao.upsertNote(noteWithId.toLocal(userId).copy(isSynced = true))
                        syncPendingNotes()
                        ResultWithStatus(noteDao.getNotes().toExternal(), NoteStatus.Success)
                    } catch (e: Exception) {
                        ResultWithStatus(noteDao.getNotes().toExternal(), NoteStatus.Success)
                    }
                } else {
                    ResultWithStatus(noteDao.getNotes().toExternal(), NoteStatus.Success)
                }
            } catch (e: Exception) {
                ResultWithStatus(noteDao.getNotes().toExternal(), NoteStatus.Failure())
            }
        }
    }

    override suspend fun deleteNote(id: Int): ResultWithStatus<List<Note>> = withContext(Dispatchers.IO) {
        try {
            val userId = authRepository.getCurrentUserId()
            noteDao.markAsDeleted(id)
            deletedNoteDao.insert(DeletedNote(id))

            if (networkChecker.isOnline() && userId != null) {
                try {
                    networkSource.deleteNoteById(id)
                    noteDao.deleteNoteById(id)
                    deletedNoteDao.deleteById(id)
                    ResultWithStatus(noteDao.getNotes().toExternal(), NoteStatus.Success)
                } catch (e: Exception) {
                    ResultWithStatus(noteDao.getNotes().toExternal(), NoteStatus.Deleted)
                }
            } else {
                ResultWithStatus(noteDao.getNotes().toExternal(), NoteStatus.Deleted)
            }
        } catch (e: Exception) {
            ResultWithStatus(noteDao.getNotes().toExternal(), NoteStatus.Failure())
        }
    }

    override suspend fun getNotes(): ResultWithStatus<List<Note>> = withContext(Dispatchers.IO) {
        try {
            val userId = authRepository.getCurrentUserId()

            if (networkChecker.isOnline() && userId != null) {
                try {
                    syncFromNetwork()
                    syncPendingNotes()
                    syncPendingDeletions()
                    ResultWithStatus(noteDao.getNotes().toExternal(), NoteStatus.Success)
                } catch (e: Exception) {
                    ResultWithStatus(noteDao.getNotes().toExternal(), NoteStatus.Failure())
                }
            } else {
                ResultWithStatus(noteDao.getNotes().toExternal(), NoteStatus.Success)
            }
        } catch (e: Exception) {
            ResultWithStatus(emptyList(), NoteStatus.Failure())
        }
    }

    override suspend fun getNoteById(id: Int): ResultWithStatus<Note> = withContext(Dispatchers.IO) {
        try {
            val localNote = noteDao.getNoteById(id)
            if (localNote != null) {
                ResultWithStatus(localNote.toExternal(), NoteStatus.Success)
            } else {
                ResultWithStatus(Note(id = 0, text = "", date = ""), NoteStatus.Failure())
            }
        } catch (e: Exception) {
            ResultWithStatus(Note(id = 0, text = "", date = ""), NoteStatus.Failure())
        }
    }

    private suspend fun syncFromNetwork() {
        try {
            val networkNotes = networkSource.getNotesByUserId()
            networkNotes.forEach { networkNote ->
                noteDao.upsertNote(networkNote.toLocal())
            }
        } catch (e: Exception) {

        }
    }

    private suspend fun syncPendingNotes() {
        try {
            val unsyncedNotes = noteDao.getUnsyncedNotes()
            unsyncedNotes.forEach { localNote ->
                try {
                    networkSource.upsertNote(localNote.toNetwork())
                    noteDao.upsertNote(localNote.copy(isSynced = true))
                } catch (e: Exception) {
                }
            }
        } catch (e: Exception) {

        }
    }

    private suspend fun syncPendingDeletions() {
        try {
            val deletedNotes = deletedNoteDao.getAll()
            deletedNotes.forEach { deletedNote ->
                try {
                    networkSource.deleteNoteById(deletedNote.id)
                    noteDao.deleteNoteById(deletedNote.id)
                    deletedNoteDao.deleteById(deletedNote.id)
                } catch (e: Exception) {
                }
            }
        } catch (e: Exception) {
        }
    }

    override suspend fun fullSync(): ResultWithStatus<Unit> = withContext(Dispatchers.IO) {
        try {
            if (!networkChecker.isOnline()) {
                return@withContext ResultWithStatus(Unit, NoteStatus.Failure("Нет подключения к интернету"))
            }

            val userId = authRepository.getCurrentUserId()
                ?: return@withContext ResultWithStatus(Unit, NoteStatus.Failure("Пользователь не авторизован"))

            syncPendingDeletions()
            syncFromNetwork()
            syncPendingNotes()

            ResultWithStatus(Unit, NoteStatus.Success)
        } catch (e: Exception) {
            ResultWithStatus(Unit, NoteStatus.Failure(e.toString()))
        }
    }

    override suspend fun clearLocalData() {
        try {
            noteDao.clearAll()
            deletedNoteDao.clearAll()
        } catch (e: Exception) {
        }
    }
}