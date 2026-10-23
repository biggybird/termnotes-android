package com.termnotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.termnotes.data.NotesDatabase
import com.termnotes.data.NotesRepository
import com.termnotes.ui.NotesViewModel
import com.termnotes.ui.TermNotesApp
import com.termnotes.ui.theme.TermNotesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.BLACK
        window.navigationBarColor = android.graphics.Color.BLACK
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightNavigationBars = false

        val database = Room.databaseBuilder(
            applicationContext,
            NotesDatabase::class.java,
            "termnotes.db"
        ).build()
        val repository = NotesRepository(database.noteDao())

        setContent {
            TermNotesTheme {
                val notesViewModel: NotesViewModel = viewModel(
                    factory = NotesViewModel.Factory(repository)
                )
                TermNotesApp(viewModel = notesViewModel)
            }
        }
    }
}
