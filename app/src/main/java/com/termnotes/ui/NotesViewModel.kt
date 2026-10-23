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
import org.json.JSONArray
import org.json.JSONObject

class NotesViewModel(private val repository: NotesRepository) : ViewModel() {
    private val searchQuery = MutableStateFlow("")
    val query: StateFlow<String> = searchQuery
    val notes: StateFlow<List<Note>> = combine(repository.observeNotes(), searchQuery) { allNotes, query ->
        val term = query.trim()
        if (term.isEmpty()) allNotes else allNotes.filter { it.title.contains(term, true) || it.content.contains(term, true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun updateQuery(value: String) { searchQuery.value = value }
    fun clearQuery() { searchQuery.value = "" }

    suspend fun saveNote(noteId: Long?, titleInput: String, contentInput: String): Boolean {
        val title = titleInput.trim()
        val content = contentInput.trimEnd()
        if (title.isBlank() && content.isBlank()) return false
        val now = System.currentTimeMillis()
        val existing = if (noteId == null) null else repository.allNotes().firstOrNull { it.id == noteId }
        val finalTitle = title.ifBlank { content.lineSequence().firstOrNull()?.trim()?.take(48).orEmpty().ifBlank { "Untitled note" } }
        repository.save(Note(id = existing?.id ?: 0L, title = finalTitle, content = content, createdAt = existing?.createdAt ?: now, updatedAt = now))
        return true
    }

    fun deleteNote(note: Note) { viewModelScope.launch { repository.delete(note) } }

    suspend fun exportJson(): String {
        val array = JSONArray()
        repository.allNotes().forEach { note ->
            array.put(JSONObject().put("id", note.id).put("title", note.title).put("body", note.content).put("createdAt", note.createdAt).put("updatedAt", note.updatedAt))
        }
        return JSONObject().put("format", "TERMNOTES_BACKUP").put("version", 1).put("notes", array).toString(2)
    }

    /** Validates the whole backup before inserting anything. Existing title/body duplicates are skipped. */
    suspend fun importJson(json: String): Int {
        val root = JSONObject(json)
        require(root.optString("format") == "TERMNOTES_BACKUP") { "This file is not a TERMNOTES backup." }
        val array = root.optJSONArray("notes") ?: throw IllegalArgumentException("Backup is missing its notes list.")
        val incoming = mutableListOf<Note>()
        for (i in 0 until array.length()) {
            val item = array.optJSONObject(i) ?: throw IllegalArgumentException("Note ${i + 1} is invalid.")
            if (!item.has("title") || !item.has("body") || !item.has("createdAt") || !item.has("updatedAt")) throw IllegalArgumentException("Note ${i + 1} is missing required fields.")
            val title = item.getString("title")
            val body = item.getString("body")
            if (title.isBlank() && body.isBlank()) throw IllegalArgumentException("Note ${i + 1} has no title or body.")
            incoming += Note(id = 0L, title = title, content = body, createdAt = item.getLong("createdAt"), updatedAt = item.getLong("updatedAt"))
        }
        val existing = repository.allNotes().map { it.title.trim().lowercase() to it.content.trim() }.toSet()
        val unique = incoming.distinctBy { it.title.trim().lowercase() to it.content.trim() }.filterNot { (it.title.trim().lowercase() to it.content.trim()) in existing }
        if (unique.isNotEmpty()) repository.importNotes(unique)
        return unique.size
    }

    class Factory(private val repository: NotesRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NotesViewModel::class.java)) return NotesViewModel(repository) as T
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
