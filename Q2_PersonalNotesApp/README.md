# PersonalNotesApp

**Assignment:** Q2 - Personal Notes Application
**Status:** Completed

## Features Implemented
- **DataStore Preferences:** Stores the username and Dark/Light theme preference permanently.
- **File Storage (Scoped Storage):** Notes are saved securely as text files in the app's internal `filesDir`. Follows Scoped Storage guidelines and requires no invasive permissions like `MANAGE_EXTERNAL_STORAGE`.
- **Runtime Permissions:** Implemented a prompt for `POST_NOTIFICATIONS` to demonstrate Android 13+ runtime permissions as required.
- **UI Screens:** Welcome Screen, Notes Dashboard, Create/Edit Note, Settings Screen.
- **Delete Confirmation:** Shows an `AlertDialog` before deleting a note.
- **Search:** Search bar filters notes dynamically by title and content.

## Technologies Used
- Kotlin
- Jetpack Compose
- Material Design 3
- DataStore Preferences
- Internal File Storage (Standard Java I/O)
- Gradle Kotlin DSL

## Project Structure
- `app/src/main/java/com/example/personalnotesapp/MainActivity.kt`: Contains all code for UI, DataStore repository, and Note file storage repository.
- `app/build.gradle.kts`: Includes DataStore dependency (`androidx.datastore:datastore-preferences`).

## How to Open in Android Studio
1. Open Android Studio.
2. Click on **File > Open**.
3. Navigate to `CSE225_CA2/Q2_PersonalNotesApp` and select it.
4. Wait for Gradle sync to complete.

## How to Run
1. Connect an Android emulator or a physical device.
2. Click the **Run 'app'** button (green play icon) in the Android Studio toolbar.

## Emulator/Device Requirements
- Minimum SDK: API 24 (Android 7.0)
- Target SDK: API 36

## Important Files for Code Screenshots
- `MainActivity.kt`: Take screenshots of the `UserPreferences` class, `NoteRepository` class, and the composables (`NotesApp`, `WelcomeScreen`, `HomeScreen`).

## Required Output Screenshots
1. **Welcome Screen:** Showing the username input and theme toggle.
2. **Runtime Permission Prompt:** If running on API 33+, the notification permission prompt.
3. **Empty Home Screen:** Showing the greeting and FAB.
4. **Create Note Screen:** Entering a note.
5. **Home Screen with Notes:** Showing the populated list of notes.
6. **Settings Screen:** Showing the ability to toggle the theme and change the name.
