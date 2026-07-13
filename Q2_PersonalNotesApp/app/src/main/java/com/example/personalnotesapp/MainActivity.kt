package com.example.personalnotesapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.personalnotesapp.ui.theme.personalnotesappTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val userPreferences = remember { UserPreferences(context) }
            val themePreference by userPreferences.themeFlow.collectAsState(initial = true) // true for dark, false for light, but let's use standard Light by default
            
            personalnotesappTheme(darkTheme = themePreference) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NotesApp(userPreferences)
                }
            }
        }
    }
}

class UserPreferences(private val context: Context) {
    companion object {
        val USERNAME_KEY = stringPreferencesKey("username")
        val THEME_KEY = booleanPreferencesKey("is_dark_theme")
        val IS_FIRST_TIME_KEY = booleanPreferencesKey("is_first_time")
    }

    val usernameFlow: Flow<String> = context.dataStore.data.map { it[USERNAME_KEY] ?: "" }
    val themeFlow: Flow<Boolean> = context.dataStore.data.map { it[THEME_KEY] ?: false }
    val isFirstTimeFlow: Flow<Boolean> = context.dataStore.data.map { it[IS_FIRST_TIME_KEY] ?: true }

    suspend fun saveUsername(name: String) {
        context.dataStore.edit { it[USERNAME_KEY] = name }
    }

    suspend fun saveTheme(isDark: Boolean) {
        context.dataStore.edit { it[THEME_KEY] = isDark }
    }

    suspend fun setFirstTimeCompleted() {
        context.dataStore.edit { it[IS_FIRST_TIME_KEY] = false }
    }
}

data class Note(val id: String, val title: String, val content: String, val timestamp: Long)

class NoteRepository(private val context: Context) {
    private val notesDir = File(context.filesDir, "notes").apply { mkdirs() }

    fun saveNote(title: String, content: String): Note {
        val id = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis()
        val noteFile = File(notesDir, "$id.txt")
        noteFile.writeText("$title\n---\n$content\n---\n$timestamp")
        return Note(id, title, content, timestamp)
    }

    fun updateNote(id: String, title: String, content: String) {
        val timestamp = System.currentTimeMillis()
        val noteFile = File(notesDir, "$id.txt")
        noteFile.writeText("$title\n---\n$content\n---\n$timestamp")
    }

    fun deleteNote(id: String) {
        File(notesDir, "$id.txt").delete()
    }

    fun getAllNotes(): List<Note> {
        val files = notesDir.listFiles() ?: return emptyList()
        return files.mapNotNull { file ->
            try {
                val parts = file.readText().split("\n---\n")
                if (parts.size >= 3) {
                    Note(file.nameWithoutExtension, parts[0], parts[1], parts[2].toLong())
                } else null
            } catch (e: Exception) {
                null
            }
        }.sortedByDescending { it.timestamp }
    }
}

enum class Screen { WELCOME, HOME, ADD_NOTE, EDIT_NOTE, SETTINGS }

@Composable
fun NotesApp(userPreferences: UserPreferences) {
    val isFirstTime by userPreferences.isFirstTimeFlow.collectAsState(initial = null)
    
    if (isFirstTime == null) return // Loading state
    
    var currentScreen by remember { mutableStateOf(if (isFirstTime == true) Screen.WELCOME else Screen.HOME) }
    var selectedNote by remember { mutableStateOf<Note?>(null) }

    when (currentScreen) {
        Screen.WELCOME -> WelcomeScreen(
            userPreferences,
            onComplete = { currentScreen = Screen.HOME }
        )
        Screen.HOME -> HomeScreen(
            userPreferences,
            onAddNote = { currentScreen = Screen.ADD_NOTE },
            onEditNote = { note ->
                selectedNote = note
                currentScreen = Screen.EDIT_NOTE
            },
            onOpenSettings = { currentScreen = Screen.SETTINGS }
        )
        Screen.ADD_NOTE -> NoteEditorScreen(
            note = null,
            onSave = { currentScreen = Screen.HOME },
            onBack = { currentScreen = Screen.HOME }
        )
        Screen.EDIT_NOTE -> NoteEditorScreen(
            note = selectedNote,
            onSave = { currentScreen = Screen.HOME },
            onBack = { currentScreen = Screen.HOME }
        )
        Screen.SETTINGS -> SettingsScreen(
            userPreferences,
            onBack = { currentScreen = Screen.HOME }
        )
    }
}

@Composable
fun WelcomeScreen(userPreferences: UserPreferences, onComplete: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var isDarkTheme by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    
    // Runtime Permission for POST_NOTIFICATIONS as required by the assignment to demonstrate permission usage
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "App Icon",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text("Welcome to Personal Notes", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Let's get everything set up.", style = MaterialTheme.typography.bodyLarge)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("What should we call you?") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Use Dark Theme", style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = isDarkTheme,
                onCheckedChange = { isDarkTheme = it }
            )
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = {
                if (username.isNotBlank()) {
                    coroutineScope.launch {
                        userPreferences.saveUsername(username)
                        userPreferences.saveTheme(isDarkTheme)
                        userPreferences.setFirstTimeCompleted()
                        onComplete()
                    }
                } else {
                    Toast.makeText(context, "Please enter your name", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Get Started")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userPreferences: UserPreferences,
    onAddNote: () -> Unit,
    onEditNote: (Note) -> Unit,
    onOpenSettings: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { NoteRepository(context) }
    var notes by remember { mutableStateOf(repository.getAllNotes()) }
    val username by userPreferences.usernameFlow.collectAsState(initial = "")
    var searchQuery by remember { mutableStateOf("") }
    
    var showDeleteDialog by remember { mutableStateOf<Note?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Good Morning, $username", style = MaterialTheme.typography.titleMedium)
                        Text("Your thoughts, organized.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddNote, containerColor = MaterialTheme.colorScheme.primaryContainer) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search notes...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                shape = RoundedCornerShape(12.dp)
            )

            val filteredNotes = notes.filter { 
                it.title.contains(searchQuery, ignoreCase = true) || 
                it.content.contains(searchQuery, ignoreCase = true) 
            }

            if (filteredNotes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No notes found.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredNotes) { note ->
                        NoteCard(
                            note = note,
                            onClick = { onEditNote(note) },
                            onDelete = { showDeleteDialog = note }
                        )
                    }
                }
            }
        }
    }

    if (showDeleteDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Note") },
            text = { Text("Are you sure you want to delete this note?") },
            confirmButton = {
                TextButton(onClick = {
                    repository.deleteNote(showDeleteDialog!!.id)
                    notes = repository.getAllNotes()
                    showDeleteDialog = null
                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun NoteCard(note: Note, onClick: () -> Unit, onDelete: () -> Unit) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = note.title.ifEmpty { "Untitled" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Note", tint = MaterialTheme.colorScheme.error)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = dateFormat.format(Date(note.timestamp)),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(note: Note?, onSave: () -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    val repository = remember { NoteRepository(context) }
    var title by remember { mutableStateOf(note?.title ?: "") }
    var content by remember { mutableStateOf(note?.content ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (note == null) "Create Note" else "Edit Note") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    IconButton(onClick = {
                        if (title.isNotBlank() || content.isNotBlank()) {
                            if (note == null) {
                                repository.saveNote(title, content)
                            } else {
                                repository.updateNote(note.id, title, content)
                            }
                            onSave()
                        } else {
                            Toast.makeText(context, "Note cannot be empty", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "Save Note", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Note Title", style = MaterialTheme.typography.titleLarge) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.titleLarge,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                placeholder = { Text("Start typing your note...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(userPreferences: UserPreferences, onBack: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var username by remember { mutableStateOf("") }
    val currentTheme by userPreferences.themeFlow.collectAsState(initial = false)
    val currentUsername by userPreferences.usernameFlow.collectAsState(initial = "")
    
    LaunchedEffect(currentUsername) {
        username = currentUsername
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Profile", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { 
                coroutineScope.launch { userPreferences.saveUsername(username) }
            }, modifier = Modifier.align(Alignment.End)) {
                Text("Update Name")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Appearance", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Dark Theme", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = currentTheme,
                    onCheckedChange = { isDark ->
                        coroutineScope.launch { userPreferences.saveTheme(isDark) }
                    }
                )
            }
        }
    }
}
