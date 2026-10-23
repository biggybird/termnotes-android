package com.biggybird.termnotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONArray
import org.json.JSONObject

private val Black = Color(0xFF000000)
private val Panel = Color(0xFF101510)
private val Green = Color(0xFF55FF88)
private val Muted = Color(0xFF8BAA93)

data class Note(val id: Long, val title: String, val body: String, val updated: Long)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { TermNotesApp() }
    }
}

@Composable
fun TermNotesApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember { context.getSharedPreferences("termnotes", 0) }
    val notes = remember {
        mutableStateListOf<Note>().apply {
            val raw = prefs.getString("notes", "[]") ?: "[]"
            try {
                val array = JSONArray(raw)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    add(Note(obj.getLong("id"), obj.optString("title"), obj.optString("body"), obj.optLong("updated")))
                }
            } catch (_: Exception) { clear() }
        }
    }
    var query by remember { mutableStateOf("") }
    var command by remember { mutableStateOf("") }
    var output by remember { mutableStateOf("TermNotes v1.0\nOffline notes ready. Type 'help' to begin.") }
    var editing by remember { mutableStateOf<Note?>(null) }
    var showEditor by remember { mutableStateOf(false) }
    var draftTitle by remember { mutableStateOf("") }
    var draftBody by remember { mutableStateOf("") }
    var deleteTarget by remember { mutableStateOf<Note?>(null) }

    fun persist() {
        val array = JSONArray()
        notes.forEach { n ->
            array.put(JSONObject().put("id", n.id).put("title", n.title).put("body", n.body).put("updated", n.updated))
        }
        prefs.edit().putString("notes", array.toString()).apply()
    }
    fun openNew() {
        editing = null
        draftTitle = ""
        draftBody = ""
        showEditor = true
    }
    fun runCommand(raw: String) {
        val parts = raw.trim().split(Regex("\\s+"), limit = 2)
        val action = parts.firstOrNull()?.lowercase().orEmpty()
        val arg = parts.getOrElse(1) { "" }
        when (action) {
            "help" -> output = "COMMANDS\nhelp   show commands\nlist   list saved notes\nnew    create a note\nsearch <text>  find notes\nclear  clear terminal output"
            "list" -> output = if (notes.isEmpty()) "No notes saved yet." else notes.joinToString("\n") { "• ${it.title.ifBlank { "Untitled" }}" }
            "new" -> openNew()
            "search" -> { query = arg; output = if (arg.isBlank()) "Usage: search <text>" else "Showing matches for: $arg" }
            "clear" -> output = ""
            "" -> Unit
            else -> output = "Unknown command: $action\nType 'help' for commands."
        }
    }

    MaterialTheme(colorScheme = darkColorScheme(
        background = Black, surface = Panel, primary = Green, onPrimary = Black,
        onBackground = Green, onSurface = Green, secondary = Muted
    )) {
        Surface(modifier = Modifier.fillMaxSize(), color = Black) {
            Column(Modifier.fillMaxSize().padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("TERM", color = Green, fontFamily = FontFamily.Monospace, fontSize = 25.sp)
                    Text("NOTES", color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 25.sp)
                    Spacer(Modifier.weight(1f))
                    Text("● OFFLINE", color = Muted, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                }
                Text("your thoughts, locally stored", color = Muted, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                Spacer(Modifier.height(16.dp))
                Column(Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(12.dp)).padding(12.dp)) {
                    Text("user@termnotes:~$", color = Green, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                    Text(output, color = Muted, fontFamily = FontFamily.Monospace, fontSize = 12.sp, lineHeight = 18.sp)
                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("› ", color = Green, fontFamily = FontFamily.Monospace)
                        Box(Modifier.weight(1f)) {
                            if (command.isEmpty()) Text("type a command...", color = Muted, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
                            BasicTextField(
                                value = command, onValueChange = { command = it },
                                textStyle = TextStyle(color = Green, fontFamily = FontFamily.Monospace, fontSize = 13.sp),
                                singleLine = true, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = { runCommand(command); command = "" }),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Text("RUN", color = Green, fontSize = 11.sp, fontFamily = FontFamily.Monospace,
                            modifier = Modifier.clickable { runCommand(command); command = "" }.padding(6.dp))
                    }
                }
                Spacer(Modifier.height(18.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("YOUR NOTES", color = Color.White, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
                    Spacer(Modifier.weight(1f))
                    Text("+ NEW NOTE", color = Green, fontSize = 12.sp, fontFamily = FontFamily.Monospace,
                        modifier = Modifier.clickable { openNew() }.padding(8.dp))
                }
                Spacer(Modifier.height(8.dp))
                BasicTextField(
                    value = query, onValueChange = { query = it },
                    textStyle = TextStyle(color = Green, fontFamily = FontFamily.Monospace, fontSize = 13.sp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(8.dp)).padding(12.dp),
                    decorationBox = { inner ->
                        if (query.isEmpty()) Text("⌕  Search notes...", color = Muted, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
                        inner()
                    }
                )
                Spacer(Modifier.height(8.dp))
                val visibleNotes = notes.filter {
                    it.title.contains(query, true) || it.body.contains(query, true)
                }.sortedByDescending { it.updated }
                if (visibleNotes.isEmpty()) {
                    Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        Text(if (query.isBlank()) "No notes yet.\nTap + NEW NOTE to create one." else "No matching notes found.",
                            color = Muted, fontFamily = FontFamily.Monospace, fontSize = 13.sp, lineHeight = 21.sp)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                        items(visibleNotes, key = { it.id }) { note ->
                            Column(Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(10.dp))
                                .clickable { editing = note; draftTitle = note.title; draftBody = note.body; showEditor = true }
                                .padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(note.title.ifBlank { "Untitled" }, color = Green, fontFamily = FontFamily.Monospace, fontSize = 15.sp, modifier = Modifier.weight(1f))
                                    Text("×", color = Muted, fontSize = 20.sp, modifier = Modifier.clickable { deleteTarget = note }.padding(horizontal = 5.dp))
                                }
                                if (note.body.isNotBlank()) {
                                    Spacer(Modifier.height(5.dp))
                                    Text(note.body.take(120), color = Muted, fontSize = 12.sp, maxLines = 3)
                                }
                            }
                        }
                    }
                }
                Text("LOCAL STORAGE • ${notes.size} NOTES", color = Muted, fontFamily = FontFamily.Monospace, fontSize = 10.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp))
            }
        }
    }

    if (showEditor) {
        AlertDialog(
            onDismissRequest = { showEditor = false },
            containerColor = Panel,
            title = { Text(if (editing == null) "New note" else "Edit note", color = Green, fontFamily = FontFamily.Monospace) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(value = draftTitle, onValueChange = { draftTitle = it }, label = { Text("Title") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Green, unfocusedTextColor = Green,
                            focusedBorderColor = Green, unfocusedBorderColor = Muted, focusedLabelColor = Green, unfocusedLabelColor = Muted))
                    OutlinedTextField(value = draftBody, onValueChange = { draftBody = it }, label = { Text("Write something...") },
                        minLines = 5, colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Green, unfocusedTextColor = Green,
                            focusedBorderColor = Green, unfocusedBorderColor = Muted, focusedLabelColor = Green, unfocusedLabelColor = Muted))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val now = System.currentTimeMillis()
                    if (editing == null) notes.add(Note(now, draftTitle.trim(), draftBody, now))
                    else {
                        val index = notes.indexOfFirst { it.id == editing!!.id }
                        if (index >= 0) notes[index] = editing!!.copy(title = draftTitle.trim(), body = draftBody, updated = now)
                    }
                    persist()
                    showEditor = false
                    output = "Saved locally ✓"
                }) { Text("SAVE", color = Green) }
            },
            dismissButton = { TextButton(onClick = { showEditor = false }) { Text("CANCEL", color = Muted) } }
        )
    }
    deleteTarget?.let { note ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            containerColor = Panel,
            title = { Text("Delete note?", color = Green) },
            text = { Text("This note will be removed from this device.", color = Muted) },
            confirmButton = { TextButton(onClick = { notes.remove(note); persist(); deleteTarget = null }) { Text("DELETE", color = Green) } },
            dismissButton = { TextButton(onClick = { deleteTarget = null }) { Text("CANCEL", color = Muted) } }
        )
    }
}
