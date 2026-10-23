package com.termnotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.termnotes.data.NotesDatabase
import com.termnotes.data.NotesRepository
import com.termnotes.ui.AppPreferences
import com.termnotes.ui.NotesViewModel
import com.termnotes.ui.TermNotesApp
import com.termnotes.ui.theme.TermNotesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val database = Room.databaseBuilder(applicationContext, NotesDatabase::class.java, "termnotes.db").build()
        val repository = NotesRepository(database.noteDao())
        setContent {
            var selectedTheme by mutableStateOf(AppPreferences.theme(this))
            var selectedFont by mutableStateOf(AppPreferences.font(this))
            TermNotesTheme(themeName = selectedTheme) {
                val notesViewModel: NotesViewModel = viewModel(factory = NotesViewModel.Factory(repository))
                TermNotesApp(
                    viewModel = notesViewModel,
                    themeName = selectedTheme,
                    fontName = selectedFont,
                    onThemeChange = { selectedTheme = it; AppPreferences.setTheme(this, it) },
                    onFontChange = { selectedFont = it; AppPreferences.setFont(this, it) }
                )
            }
        }
    }
}
