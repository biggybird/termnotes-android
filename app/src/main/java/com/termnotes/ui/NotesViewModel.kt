package com.termnotes.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.termnotes.data.Note
import com.termnotes.data.NotesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(private val repository: NotesRepository) : ViewModel() {
    private val searchQuery = MutableStateFlow("")
    val query: StateFlow<String> = searchQuery

    val notes: StateFlow<List<Note>> = combine(repository.observeNotes(), searchQuery) { allNotes, query ->
        val term = query.trim()
        if (term.isEmpty()) allNotes
        else allNotes.filter { it.title.contains(term, true) || it.content.contains(term, true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun updateQuery(value: String) {
        searchQuery.value = value
    }

    suspend fun saveNote(noteId: Long?, titleInput: String, contentInput: String): Boolean {
        val title = titleInput.trim()
        val content = contentInput.trimEnd()
        if (title.isBlank() && content.isBlank()) return false

        val now = System.currentTimeMillis()
        val existing = if (noteId == null) null else notes.value.firstOrNull { it.id == noteId }
        val finalTitle = title.ifBlank {
            content.lineSequence().firstOrNull()?.trim()?.take(48).orEmpty().ifBlank { "Untitled note" }
        }
        repository.save(
            Note(
                id = existing?.id ?: 0L,
                title = finalTitle,
                content = content,
                createdAt = existing?.createdAt ?: now,
                updatedAt = now
            )
        )
        return true
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch { repository.delete(note) }
    }

    class Factory(private val repository: NotesRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NotesViewModel::class.java)) {
                return NotesViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
