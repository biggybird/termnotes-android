package com.termnotes.data

import kotlinx.coroutines.flow.Flow

class NotesRepository(private val dao: NoteDao) {
    fun observeNotes(): Flow<List<Note>> = dao.observeAll()

    suspend fun save(note: Note): Long {
        return if (note.id == 0L) dao.insert(note) else {
            dao.update(note)
            note.id
        }
    }

    suspend fun delete(note: Note) = dao.delete(note)
}
