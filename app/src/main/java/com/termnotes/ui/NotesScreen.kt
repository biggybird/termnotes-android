package com.termnotes.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.view.KeyEvent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.input.key.*
import com.termnotes.data.Note
import com.termnotes.ui.theme.paletteFor
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val themes = listOf("Hacker Green", "Amber CRT", "Matrix Blue", "Retro White")
private val fonts = listOf("System Monospace", "System Serif", "System Sans Serif", "Nothing CRT")

@Composable
fun TermNotesApp(
    viewModel: NotesViewModel,
    themeName: String,
    fontName: String,
    onThemeChange: (String) -> Unit,
    onFontChange: (String) -> Unit
) {
    val notes by viewModel.notes.collectAsState()
    val query by viewModel.query.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val palette = paletteFor(themeName)
    var command by remember { mutableStateOf("") }
    var output by remember { mutableStateOf("Type help to see available commands.") }
    var history by remember { mutableStateOf(listOf<String>()) }
    var historyIndex by remember { mutableIntStateOf(-1) }
    var draftBeforeHistory by remember { mutableStateOf("") }
    var editingNote by remember { mutableStateOf<Note?>(null) }
    var isCreating by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showThemePicker by remember { mutableStateOf(false) }
    var showFontPicker by remember { mutableStateOf(false) }
    var crtEnabled by remember { mutableStateOf(AppPreferences.crt(context)) }
    var soundEnabled by remember { mutableStateOf(AppPreferences.sound(context)) }
    var message by remember { mutableStateOf<String?>(null) }
    val saveTone = remember(context) { runCatching { android.media.ToneGenerator(android.media.AudioManager.STREAM_NOTIFICATION, 35) }.getOrNull() }
    DisposableEffect(saveTone) { onDispose { runCatching { saveTone?.release() } } }

    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri != null) scope.launch {
            runCatching {
                val json = viewModel.exportJson()
                context.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use { it.write(json) }
                    ?: error("Could not open the selected file.")
            }.onSuccess { message = "Backup exported successfully." }
                .onFailure { message = "Export failed: ${it.message ?: "unknown error"}" }
        }
    }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) scope.launch {
            runCatching {
                val json = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                    ?: error("Could not read the selected file.")
                viewModel.importJson(json)
            }.onSuccess { message = "Imported ${it} new notes. Existing duplicates were skipped." }
                .onFailure { message = "Import failed. Existing notes were left unchanged. ${it.message ?: ""}" }
        }
    }

    val mono = when (fontName) {
        "System Serif" -> FontFamily.Serif
        "System Sans Serif" -> FontFamily.SansSerif
        "Nothing CRT" -> FontFamily(Font(R.font.nothing_crt))
        else -> FontFamily.Monospace
    }

    Surface(Modifier.fillMaxSize(), color = palette.background) {
        Box(Modifier.fillMaxSize()) {
            Column(
                Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)
                    .imePadding().padding(horizontal = 18.dp, vertical = 12.dp)
            ) {
                BoxWithConstraints(Modifier.fillMaxWidth()) {
                    if (maxWidth < 430.dp) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("TERMNOTES", color = palette.accent, fontFamily = mono, fontWeight = FontWeight.Bold, fontSize = 23.sp, maxLines = 1, softWrap = false)
                            Text("LOCAL STORAGE / OFFLINE", color = palette.muted, fontFamily = mono, fontSize = 9.sp, maxLines = 1)
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                                TextButton(onClick = { showSettings = true }, contentPadding = PaddingValues(horizontal = 8.dp)) { Text("⚙ SETTINGS", color = palette.accent, fontFamily = mono, fontSize = 11.sp, maxLines = 1) }
                                Button(onClick = { isCreating = true }, contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp), colors = ButtonDefaults.buttonColors(containerColor = palette.accent, contentColor = palette.background), shape = RoundedCornerShape(10.dp)) { Text("+ NEW", fontFamily = mono, fontWeight = FontWeight.Bold, maxLines = 1) }
                            }
                        }
                    } else {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(Modifier.weight(1f)) {
                                Text("TERMNOTES", color = palette.accent, fontFamily = mono, fontWeight = FontWeight.Bold, fontSize = 23.sp, maxLines = 1, softWrap = false)
                                Text("LOCAL STORAGE / OFFLINE", color = palette.muted, fontFamily = mono, fontSize = 9.sp, maxLines = 1)
                            }
                            TextButton(onClick = { showSettings = true }, contentPadding = PaddingValues(horizontal = 8.dp)) { Text("⚙ SETTINGS", color = palette.accent, fontFamily = mono, fontSize = 11.sp, maxLines = 1) }
                            Button(onClick = { isCreating = true }, contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp), colors = ButtonDefaults.buttonColors(containerColor = palette.accent, contentColor = palette.background), shape = RoundedCornerShape(10.dp)) { Text("+ NEW", fontFamily = mono, fontWeight = FontWeight.Bold, maxLines = 1) }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = command, onValueChange = { command = it; historyIndex = -1 },
                    textStyle = LocalTextStyle.current.copy(fontFamily = mono),
                    modifier = Modifier.fillMaxWidth().onPreviewKeyEvent { event ->
                        if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                        when (event.nativeKeyEvent.keyCode) {
                            KeyEvent.KEYCODE_DPAD_UP -> {
                                if (history.isNotEmpty()) {
                                    if (historyIndex == -1) draftBeforeHistory = command
                                    historyIndex = (if (historyIndex == -1) history.lastIndex else (historyIndex - 1).coerceAtLeast(0))
                                    command = history[historyIndex]
                                }
                                true
                            }
                            KeyEvent.KEYCODE_DPAD_DOWN -> {
                                if (historyIndex >= 0) {
                                    historyIndex++
                                    command = if (historyIndex >= history.size) { historyIndex = -1; draftBeforeHistory } else history[historyIndex]
                                }
                                true
                            }
                            else -> false
                        }
                    },
                    singleLine = true,
                    placeholder = { Text("command or search...", color = palette.muted, fontFamily = mono) },
                    leadingIcon = { Text(">", color = palette.accent, fontFamily = mono, fontWeight = FontWeight.Bold) },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = androidx.compose.ui.text.input.ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        val raw = command.trim()
                        if (raw.isNotEmpty()) {
                            if (history.lastOrNull() != raw) history = (history + raw).takeLast(50)
                            when (val parsed = CommandProcessor.parse(raw)) {
                                else -> when (parsed.action) {
                                    ParsedCommand.Action.HELP -> output = "help / :help  ·  list  ·  new / :new\nsearch <text>  ·  clear / :clear  ·  :theme"
                                    ParsedCommand.Action.LIST -> { viewModel.clearQuery(); output = "Showing all saved notes." }
                                    ParsedCommand.Action.NEW -> isCreating = true
                                    ParsedCommand.Action.THEME -> showThemePicker = true
                                    ParsedCommand.Action.CLEAR -> { viewModel.clearQuery(); output = "Terminal output cleared."; }
                                    ParsedCommand.Action.SEARCH -> { viewModel.updateQuery(parsed.argument); output = "Searching notes for: ${parsed.argument}" }
                                    ParsedCommand.Action.UNKNOWN -> output = "Unknown command. Type help to see available commands."
                                    ParsedCommand.Action.EMPTY -> Unit
                                }
                            }
                        }
                        command = ""
                    }),
                    colors = terminalFieldColors(palette), shape = RoundedCornerShape(12.dp)
                )
                if (output.isNotBlank()) {
                    Text(output, modifier = Modifier.padding(top = 7.dp, bottom = 4.dp), color = palette.muted, fontFamily = mono, fontSize = 11.sp)
                }
                Row(Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("YOUR NOTES", color = palette.muted, fontFamily = mono, fontSize = 11.sp)
                    Text("${notes.size} ENTRIES", color = palette.accent, fontFamily = mono, fontSize = 11.sp)
                }
                HorizontalDivider(color = palette.border)
                if (notes.isEmpty()) {
                    Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(if (query.isBlank()) "NO NOTES FOUND" else "NO MATCHES FOUND", color = palette.accent, fontFamily = mono, fontSize = 15.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(if (query.isBlank()) "Tap + NEW or type :new." else "Try another search term.", color = palette.muted, fontSize = 13.sp)
                        }
                    }
                } else {
                    LazyColumn(Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(vertical = 12.dp)) {
                        items(notes, key = { it.id }) { note -> NoteCard(note, palette, mono) { editingNote = note } }
                    }
                }
                HorizontalDivider(color = palette.border)
                Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("TERMNOTES v1.1.0", color = palette.muted, fontFamily = mono, fontSize = 10.sp)
                    Text("● OFFLINE", color = palette.accent, fontFamily = mono, fontSize = 10.sp)
                }
            }
            if (crtEnabled) {
                Canvas(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
                    val gap = 5.dp.toPx()
                    var y = 0f
                    while (y < size.height) {
                        drawLine(
                            color = (if (palette.isLight) Color.Black else Color.White).copy(alpha = if (palette.isLight) 0.045f else 0.10f),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                        y += gap
                    }
                }
            }
        }
    }

    if (isCreating || editingNote != null) {
        val selected = editingNote
        NoteEditorDialog(
            note = selected, palette = palette, font = mono,
            onDismiss = { editingNote = null; isCreating = false },
            onSave = { title, body ->
                scope.launch {
                    runCatching { viewModel.saveNote(selected?.id, title, body) }
                        .onSuccess { saved ->
                            if (saved) {
                                editingNote = null
                                isCreating = false
                                message = "Note saved."
                                if (soundEnabled) runCatching { saveTone?.startTone(android.media.ToneGenerator.TONE_PROP_ACK, 85) }
                            } else message = "A title or note body is required."
                        }
                        .onFailure { error -> message = "Save failed. Your editor is still open. " + (error.message ?: "") }
                }
            },
            onDelete = selected?.let { note -> { viewModel.deleteNote(note); editingNote = null; isCreating = false; message = "Note deleted." } }
        )
    }

    if (showSettings) {
        AlertDialog(
            onDismissRequest = { showSettings = false }, containerColor = palette.panel,
            title = { Text("SETTINGS", color = palette.accent, fontFamily = mono, fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { showThemePicker = true }, modifier = Modifier.fillMaxWidth()) { Text("Theme: $themeName", color = palette.accent, fontFamily = mono) }
                    OutlinedButton(onClick = { showFontPicker = true }, modifier = Modifier.fillMaxWidth()) { Text("Font: $fontName", color = palette.accent, fontFamily = mono) }
                    Row(verticalAlignment = Alignment.CenterVertically) { Text("CRT scanlines", Modifier.weight(1f), color = palette.text); Switch(checked = crtEnabled, onCheckedChange = { crtEnabled = it; AppPreferences.setCrt(context, it) }, colors = SwitchDefaults.colors(checkedThumbColor = palette.background, checkedTrackColor = palette.accent, checkedBorderColor = palette.accent, uncheckedThumbColor = palette.muted, uncheckedTrackColor = palette.background, uncheckedBorderColor = palette.muted)) }
                    Row(verticalAlignment = Alignment.CenterVertically) { Text("Save confirmation sound", Modifier.weight(1f), color = palette.text); Switch(checked = soundEnabled, onCheckedChange = { soundEnabled = it; AppPreferences.setSound(context, it) }, colors = SwitchDefaults.colors(checkedThumbColor = palette.background, checkedTrackColor = palette.accent, checkedBorderColor = palette.accent, uncheckedThumbColor = palette.muted, uncheckedTrackColor = palette.background, uncheckedBorderColor = palette.muted)) }
                    OutlinedButton(onClick = { exportLauncher.launch("termnotes-backup.json") }, modifier = Modifier.fillMaxWidth()) { Text("EXPORT JSON BACKUP", color = palette.accent, fontFamily = mono) }
                    OutlinedButton(onClick = { importLauncher.launch(arrayOf("application/json", "text/*", "*/*")) }, modifier = Modifier.fillMaxWidth()) { Text("IMPORT JSON (MERGE)", color = palette.accent, fontFamily = mono) }
                    HorizontalDivider(color = palette.border)
                    Text("DEVELOPER", color = palette.accent, fontFamily = mono, fontWeight = FontWeight.Bold)
                    val versionName = remember(context) { runCatching {
                        @Suppress("DEPRECATION")
                        context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "Unknown"
                    }.getOrDefault("Unknown") }
                    Text("Developer: @biggybird", color = palette.text, fontFamily = mono, fontSize = 12.sp)
                    Text("App: TERMNOTES", color = palette.text, fontFamily = mono, fontSize = 12.sp)
                    Text("Version: $versionName", color = palette.text, fontFamily = mono, fontSize = 12.sp)
                    Text("Platform: Native Android · Kotlin · Jetpack Compose", color = palette.text, fontFamily = mono, fontSize = 12.sp)
                    Text("Storage: Local / offline", color = palette.text, fontFamily = mono, fontSize = 12.sp)
                    Text("TERMNOTES is an offline-first terminal-inspired notes app. Notes stay on this device unless exported.", color = palette.muted, fontSize = 11.sp, fontFamily = mono)
                    OutlinedButton(onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://github.com/biggybird/termnotes-android"))
                        runCatching { context.startActivity(intent) }.onFailure { message = "No browser app is available to open the repository." }
                    }, modifier = Modifier.fillMaxWidth()) { Text("OPEN GITHUB REPOSITORY ↗", color = palette.accent, fontFamily = mono) }
                }
            },
            confirmButton = { TextButton(onClick = { showSettings = false }) { Text("DONE", color = palette.accent, fontFamily = mono) } }
        )
    }
    if (showThemePicker) {
        AlertDialog(onDismissRequest = { showThemePicker = false }, containerColor = palette.panel,
            title = { Text("SELECT THEME", color = palette.accent, fontFamily = mono) },
            text = { Column { themes.forEach { item -> Text(if (item == themeName) "● $item" else "○ $item", Modifier.fillMaxWidth().clickable { onThemeChange(item); showThemePicker = false }.padding(12.dp), color = palette.text, fontFamily = mono) } } },
            confirmButton = { TextButton(onClick = { showThemePicker = false }) { Text("CLOSE", color = palette.accent) } })
    }
    if (showFontPicker) {
        AlertDialog(onDismissRequest = { showFontPicker = false }, containerColor = palette.panel,
            title = { Text("SELECT FONT", color = palette.accent, fontFamily = mono) },
            text = { Column { fonts.forEach { item -> Text(if (item == fontName) "● $item" else "○ $item", Modifier.fillMaxWidth().clickable { onFontChange(item); showFontPicker = false }.padding(12.dp), color = palette.text, fontFamily = mono) } } },
            confirmButton = { TextButton(onClick = { showFontPicker = false }) { Text("CLOSE", color = palette.accent) } })
    }
    message?.let { msg ->
        AlertDialog(onDismissRequest = { message = null }, containerColor = palette.panel,
            title = { Text("TERMNOTES", color = palette.accent, fontFamily = mono) },
            text = { Text(msg, color = palette.text) },
            confirmButton = { TextButton(onClick = { message = null }) { Text("OK", color = palette.accent) } })
    }
}

@SuppressLint("NonObservableLocale")
@Composable
private fun NoteCard(note: Note, palette: com.termnotes.ui.theme.TermPalette, font: FontFamily, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick), colors = CardDefaults.cardColors(containerColor = palette.panel),
        border = BorderStroke(1.dp, palette.border), shape = RoundedCornerShape(14.dp)) {
        Column(Modifier.padding(15.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(">", color = palette.accent, fontFamily = font, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(8.dp))
                Text(note.title, Modifier.weight(1f), color = palette.text, fontFamily = font, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            if (note.content.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(note.content, color = palette.text.copy(alpha = 0.78f), fontFamily = font, fontSize = 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.height(10.dp))
            Text("UPDATED  ${SimpleDateFormat("dd MMM yyyy  HH:mm", Locale.getDefault()).format(Date(note.updatedAt))}", color = palette.muted, fontFamily = font, fontSize = 10.sp)
        }
    }
}

@Composable
private fun NoteEditorDialog(note: Note?, palette: com.termnotes.ui.theme.TermPalette, font: FontFamily, onDismiss: () -> Unit, onSave: (String, String) -> Unit, onDelete: (() -> Unit)?) {
    var title by remember(note?.id) { mutableStateOf(note?.title.orEmpty()) }
    var body by remember(note?.id) { mutableStateOf(note?.content.orEmpty()) }
    var preview by remember { mutableStateOf(false) }
    var showDelete by remember { mutableStateOf(false) }
    val valid = title.isNotBlank() || body.isNotBlank()
    AlertDialog(onDismissRequest = onDismiss, containerColor = palette.panel,
        title = { Text(if (note == null) "NEW_NOTE.md" else "EDIT_NOTE.md", color = palette.accent, fontFamily = font, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(title, { title = it }, Modifier.fillMaxWidth(), textStyle = LocalTextStyle.current.copy(fontFamily = font), label = { Text("TITLE", fontFamily = font) }, singleLine = true, colors = terminalFieldColors(palette))
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { preview = false }) { Text("EDIT", color = if (!preview) palette.accent else palette.muted, fontFamily = font) }
                    TextButton(onClick = { preview = true }) { Text("PREVIEW", color = if (preview) palette.accent else palette.muted, fontFamily = font) }
                }
                if (preview) {
                    LazyColumn(Modifier.fillMaxWidth().height(250.dp).background(palette.background, RoundedCornerShape(8.dp)).padding(10.dp)) {
                        items(body.lines()) { line ->
                            val code = line.trimStart().startsWith("```")
                            val heading = line.startsWith("#")
                            Text(if (code) "" else line.replace(Regex("^#{1,6}\\s+"), "").replace(Regex("\\*\\*(.*?)\\*\\*"), "$1").replace(Regex("\\*(.*?)\\*"), "$1").replace(Regex("`([^`]+)`"), "$1"),
                                color = if (code) palette.accent else palette.text,
                                fontFamily = if (code || line.contains("`")) FontFamily.Monospace else font,
                                fontWeight = if (heading) FontWeight.Bold else FontWeight.Normal,
                                fontSize = if (heading) 16.sp else 13.sp,
                                modifier = if (code || line.trimStart().startsWith("- ") || line.trimStart().matches(Regex("\\d+\\..*"))) Modifier.fillMaxWidth().background(if (code) palette.panel else Color.Transparent).padding(vertical = 2.dp) else Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                } else {
                    OutlinedTextField(body, { body = it }, Modifier.fillMaxWidth().height(250.dp), label = { Text("MARKDOWN / TEXT") }, placeholder = { Text("Write a note...\n# Heading\n**bold**  `code`\n- list item\n```\ncode block\n```") }, colors = terminalFieldColors(palette))
                }
                Text("Markdown preview is read-only. Original text is preserved.", color = palette.muted, fontSize = 10.sp, modifier = Modifier.padding(top = 6.dp))
            }
        },
        confirmButton = { Button(onClick = { onSave(title, body) }, enabled = valid, colors = ButtonDefaults.buttonColors(containerColor = palette.accent, contentColor = palette.background)) { Text("SAVE", fontFamily = font, fontWeight = FontWeight.Bold) } },
        dismissButton = { Row(verticalAlignment = Alignment.CenterVertically) {
            if (onDelete != null) TextButton(onClick = { showDelete = true }) { Text("DELETE", color = Color(0xFFFF6B6B), fontFamily = font) }
            TextButton(onClick = onDismiss) { Text("CANCEL", color = palette.muted, fontFamily = font) }
        } }
    )
    if (showDelete && onDelete != null) AlertDialog(onDismissRequest = { showDelete = false }, containerColor = palette.panel,
        title = { Text("DELETE NOTE?", color = Color(0xFFFF6B6B), fontFamily = font) },
        text = { Text("This note will be removed from this device.", color = palette.text) },
        confirmButton = { TextButton(onClick = onDelete) { Text("DELETE", color = Color(0xFFFF6B6B)) } },
        dismissButton = { TextButton(onClick = { showDelete = false }) { Text("KEEP NOTE", color = palette.accent) } })
}

@Composable
private fun terminalFieldColors(p: com.termnotes.ui.theme.TermPalette) = OutlinedTextFieldDefaults.colors(
    focusedTextColor = p.text, unfocusedTextColor = p.text, focusedBorderColor = p.accent,
    unfocusedBorderColor = p.border, focusedLabelColor = p.accent, unfocusedLabelColor = p.muted,
    cursorColor = p.accent, focusedContainerColor = p.background, unfocusedContainerColor = p.background
)
