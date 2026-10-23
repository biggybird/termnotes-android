package com.termnotes.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.termnotes.data.Note
import com.termnotes.ui.theme.TermGreen
import com.termnotes.ui.theme.TermMuted
import com.termnotes.ui.theme.TermPanel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TermNotesApp(viewModel: NotesViewModel) {
    val notes by viewModel.notes.collectAsState()
    val query by viewModel.query.collectAsState()
    var editingNote by remember { mutableStateOf<Note?>(null) }
    var isCreating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
        Column(Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("TERMNOTES", color = TermGreen, fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold, fontSize = 25.sp)
                    Text("LOCAL STORAGE  /  OFFLINE", color = TermMuted,
                        fontFamily = FontFamily.Monospace, fontSize = 10.sp, letterSpacing = 1.sp)
                }
                Button(
                    onClick = { isCreating = true },
                    colors = ButtonDefaults.buttonColors(containerColor = TermGreen, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("+ NEW", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(22.dp))
            OutlinedTextField(
                value = query,
                onValueChange = viewModel::updateQuery,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("> SEARCH NOTES...", color = TermMuted, fontFamily = FontFamily.Monospace) },
                leadingIcon = { Text(">", color = TermGreen, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) },
                colors = fieldColors(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("YOUR NOTES", color = TermMuted, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                Text("%02d ENTRIES".format(notes.size), color = TermGreen, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
            }
            Spacer(Modifier.height(10.dp))
            Divider(color = TermPanel)

            if (notes.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            if (query.isBlank()) "NO NOTES FOUND" else "NO MATCHES FOUND",
                            color = TermGreen, fontFamily = FontFamily.Monospace, fontSize = 15.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            if (query.isBlank()) "Tap + NEW to create your first note." else "Try another search term.",
                            color = TermMuted, fontSize = 13.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    items(notes, key = { it.id }) { note ->
                        NoteCard(note = note, onClick = { editingNote = note })
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Divider(color = TermPanel)
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("TERMNOTES v1.0.0", color = TermMuted, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                Text("● OFFLINE", color = TermGreen, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
            }
        }
    }

    if (isCreating || editingNote != null) {
        val selectedNote = editingNote
        NoteEditorDialog(
            note = selectedNote,
            onDismiss = { editingNote = null; isCreating = false },
            onSave = { title, content ->
                scope.launch {
                    if (viewModel.saveNote(selectedNote?.id, title, content)) {
                        editingNote = null
                        isCreating = false
                    }
                }
            },
            onDelete = selectedNote?.let { note ->
                {
                    viewModel.deleteNote(note)
                    editingNote = null
                    isCreating = false
                }
            }
        )
    }
}

@SuppressLint("NonObservableLocale")
@Composable
private fun NoteCard(note: Note, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = TermPanel),
        border = BorderStroke(1.dp, Color(0xFF183318)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(">", color = TermGreen, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(8.dp))
                Text(
                    note.title,
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFE8FFE8),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (note.content.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(note.content, color = Color(0xFFB4C9B4), fontSize = 13.sp,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.height(12.dp))
            Text(
                "UPDATED  ${SimpleDateFormat("dd MMM yyyy  HH:mm", Locale.getDefault()).format(Date(note.updatedAt))}",
                color = TermMuted, fontFamily = FontFamily.Monospace, fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun NoteEditorDialog(
    note: Note?,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit,
    onDelete: (() -> Unit)?
) {
    var title by remember(note?.id) { mutableStateOf(note?.title.orEmpty()) }
    var content by remember(note?.id) { mutableStateOf(note?.content.orEmpty()) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val valid = title.isNotBlank() || content.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF080D08),
        titleContentColor = TermGreen,
        textContentColor = Color(0xFFE8FFE8),
        title = {
            Text(if (note == null) "NEW_NOTE.md" else "EDIT_NOTE.md",
                fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                OutlinedTextField(
                    value = title, onValueChange = { title = it }, modifier = Modifier.fillMaxWidth(),
                    label = { Text("TITLE") }, singleLine = true, colors = fieldColors()
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = content, onValueChange = { content = it },
                    modifier = Modifier.fillMaxWidth().height(210.dp),
                    label = { Text("CONTENT") }, placeholder = { Text("Write something...") },
                    colors = fieldColors()
                )
                if (!valid) {
                    Spacer(Modifier.height(8.dp))
                    Text("Enter a title or some content to save.", color = TermMuted, fontSize = 11.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(title, content) },
                enabled = valid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = TermGreen, contentColor = Color.Black,
                    disabledContainerColor = TermPanel, disabledContentColor = TermMuted
                )
            ) {
                Text("SAVE", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onDelete != null) {
                    TextButton(onClick = { showDeleteConfirmation = true }) {
                        Text("DELETE", color = Color(0xFFFF6B6B), fontFamily = FontFamily.Monospace)
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("CANCEL", color = TermMuted, fontFamily = FontFamily.Monospace)
                }
            }
        }
    )

    if (showDeleteConfirmation && onDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            containerColor = Color(0xFF080D08),
            title = { Text("DELETE NOTE?", color = Color(0xFFFF6B6B), fontFamily = FontFamily.Monospace) },
            text = { Text("This note will be removed from this device.", color = Color(0xFFE8FFE8)) },
            confirmButton = {
                TextButton(onClick = onDelete) {
                    Text("DELETE", color = Color(0xFFFF6B6B), fontFamily = FontFamily.Monospace)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("KEEP NOTE", color = TermGreen, fontFamily = FontFamily.Monospace)
                }
            }
        )
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color(0xFFE8FFE8),
    unfocusedTextColor = Color(0xFFE8FFE8),
    focusedBorderColor = TermGreen,
    unfocusedBorderColor = TermPanel,
    focusedLabelColor = TermGreen,
    unfocusedLabelColor = TermMuted,
    cursorColor = TermGreen,
    focusedContainerColor = Color.Black,
    unfocusedContainerColor = Color.Black
)
