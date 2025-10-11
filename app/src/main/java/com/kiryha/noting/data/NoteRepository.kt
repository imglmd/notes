package com.kiryha.noting.data

    import com.kiryha.noting.domain.status.NoteStatus
    import com.kiryha.noting.domain.status.ResultWithStatus
    import com.kiryha.noting.data.source.local.NoteDao
    import com.kiryha.noting.data.source.network.NetworkDataSource
    import com.kiryha.noting.domain.model.Note
    import java.util.UUID

class NoteRepository(
    private val noteDao: NoteDao,
    private val networkSource: NetworkDataSource
) : ActionWithNoteImpl {

        override suspend fun upsertNote(note: Note): ResultWithStatus<List<Note>> {
            val noteWithId = if (note.id==0) {
                note.copy(id = UUID.randomUUID().hashCode())
            } else {
                note
            }

            //noteDao.upsertNote(noteWithId.toLocal())
            networkSource.upsertNote(noteWithId.toNetwork())
            return ResultWithStatus(networkSource.getNotes().toExternal(), NoteStatus.Success)
        }

        override suspend fun deleteNote(id: Int): ResultWithStatus<List<Note>> {
            /*val note = noteDao.getNoteById(id) ?: return ResultWithStatus(emptyList(),
                NoteStatus.Failure)*/
            networkSource.deleteNoteById(id)
            return ResultWithStatus(networkSource.getNotes().toExternal(), NoteStatus.Success)
        }

        override suspend fun getNotes(): ResultWithStatus<List<Note>> {
            return ResultWithStatus(networkSource.getNotes().toExternal(), NoteStatus.Success)
        }

        override suspend fun getNoteById(id: Int): ResultWithStatus<Note> {
            val note = networkSource.getNoteById(id)
            return if (note != null) {
                ResultWithStatus(note.toExternal(), NoteStatus.Success)
            } else {
                ResultWithStatus(Note(id = 0, text = "", date = ""), NoteStatus.Failure)
            }
        }
    }