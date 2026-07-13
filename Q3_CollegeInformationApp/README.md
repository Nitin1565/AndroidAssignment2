# CollegeInformationApp

**Assignment:** Q3 - College Information App
**Status:** Completed

## Features Implemented
- **Navigation Drawer:** Uses `ModalNavigationDrawer` with a custom header (College Logo, Name, Portal text) and items for Home, Academics, Courses, Faculty, Events, etc.
- **TabRow:** Three main tabs implemented: Courses, Faculty, and Events.
- **Courses Tab:** Displays a `LazyColumn` of course cards containing course names, departments, duration, and semesters.
- **Faculty Tab:** Displays professional faculty profile cards with names, designations, and specializations.
- **Events Tab:** Displays interactive event cards with dates, times, venues, and a "Register" button.
- **Consistent UI:** Premium modern Material Design 3 interface with customized typography, proper spacing, and cards.

## Technologies Used
- Kotlin
- Jetpack Compose
- Material Design 3
- Gradle Kotlin DSL

## Project Structure
- `app/src/main/java/com/example/collegeinformationapp/MainActivity.kt`: Contains the entire UI implementation including the navigation drawer, top bar, tab row, and the respective tab contents.

## How to Open in Android Studio
1. Open Android Studio.
2. Click on **File > Open**.
3. Navigate to `CSE225_CA2/Q3_CollegeInformationApp` and select it.
4. Wait for Gradle sync to complete.

## How to Run
1. Connect an Android emulator or a physical device.
2. Click the **Run 'app'** button (green play icon) in the Android Studio toolbar.

## Emulator/Device Requirements
- Minimum SDK: API 24 (Android 7.0)
- Target SDK: API 36

## Important Files for Code Screenshots
- `MainActivity.kt`: Capture screenshots of `CollegeAppScreen` (drawer configuration), `CollegeTabRow`, and the individual tab composables (`CoursesTab`, `FacultyTab`, `EventsTab`).

## Required Output Screenshots
1. **Home Screen with Drawer Closed:** Showing the Top Bar and the "Courses" Tab content.
2. **Navigation Drawer Opened:** Showing the drawer header and the menu items.
3. **Faculty Tab:** Showing the list of faculty members.
4. **Events Tab:** Showing the list of upcoming college events.
