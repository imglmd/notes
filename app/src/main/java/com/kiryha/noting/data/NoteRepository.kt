package com.kiryha.noting.data

    import android.content.Context
    import com.kiryha.noting.domain.status.NoteStatus
    import com.kiryha.noting.domain.status.ResultWithStatus
    import com.kiryha.noting.data.source.local.NoteDao
    import com.kiryha.noting.data.source.network.NetworkDataSource
    import com.kiryha.noting.domain.model.Note
    import com.kiryha.noting.utils.NetworkChecker
    import java.util.UUID

class NoteRepository(
    private val noteDao: NoteDao,
    private val networkSource: NetworkDataSource,
    private val context: Context
) : ActionWithNoteImpl {

    private val networkChecker = NetworkChecker(context)

    override suspend fun upsertNote(note: Note): ResultWithStatus<List<Note>> {
        val noteWithId = if (note.id==0) {
            note.copy(id = UUID.randomUUID().hashCode())
        } else {
            note
        }

        noteDao.upsertNote(noteWithId.toLocal())

        return if(networkChecker.isOnline()){
            try {
                networkSource.upsertNote(noteWithId.toNetwork())
                syncFromNetwork()
                ResultWithStatus(noteDao.getNotes().toExternal(), NoteStatus.Success)
            } catch (e: Exception) {
                ResultWithStatus(noteDao.getNotes().toExternal(), NoteStatus.Failure)
            }
        } else {
            ResultWithStatus(noteDao.getNotes().toExternal(), NoteStatus.Success)
        }

    }

    override suspend fun deleteNote(id: Int): ResultWithStatus<List<Note>> {
        noteDao.deleteNoteById(id)

        return if (networkChecker.isOnline()) {
            try {
                networkSource.deleteNoteById(id)
                ResultWithStatus(noteDao.getNotes().toExternal(), NoteStatus.Success)
            } catch (e: Exception){
                ResultWithStatus(noteDao.getNotes().toExternal(), NoteStatus.Deleted)
            }
        } else {
            //надо пометить для того чтоб удалить из network в будущем
            ResultWithStatus(noteDao.getNotes().toExternal(), NoteStatus.Deleted)
        }
    }

    override suspend fun getNotes(): ResultWithStatus<List<Note>> {
        return if(networkChecker.isOnline()){
            try {
                syncFromNetwork()
                ResultWithStatus(noteDao.getNotes().toExternal(), NoteStatus.Success)
            } catch (e: Exception){
                ResultWithStatus(noteDao.getNotes().toExternal(), NoteStatus.Failure)
            }
        } else {
            ResultWithStatus(noteDao.getNotes().toExternal(), NoteStatus.Success)
        }
    }

    override suspend fun getNoteById(id: Int): ResultWithStatus<Note> {

        val localNote = noteDao.getNoteById(id)
        return if(localNote != null){
            ResultWithStatus(localNote.toExternal(), NoteStatus.Success)
        } else {
            ResultWithStatus(Note(id = 0, text = "", date = ""), NoteStatus.Failure)
        }

    }
    private suspend fun syncFromNetwork() {
        try {
            val networkNotes = networkSource.getNotesByUserId()
            networkNotes.forEach { noteDao.upsertNote(it.toLocal()) }
        } catch (e: Exception) {

        }
    }
}
