package com.termnotes.data

import kotlinx.coroutines.flow.Flow

class NotesRepository(private val dao: NoteDao) {
    fun observeNotes(): Flow<List<Note>> = dao.observeAll()
    suspend fun allNotes(): List<Note> = dao.getAll()
    suspend fun save(note: Note): Long = if (note.id == 0L) dao.insert(note) else { check(dao.update(note) == 1) { "The note could not be updated because it no longer exists." }; note.id }
    suspend fun delete(note: Note) = dao.delete(note)
    suspend fun importNotes(notes: List<Note>) = dao.insertAll(notes)
}
