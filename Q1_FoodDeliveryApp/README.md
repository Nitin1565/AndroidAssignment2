# FoodDeliveryApp

**Assignment:** Q1 - Food Delivery Home Screen
**Status:** Completed

## Features Implemented
- Modern Top App Bar with user greeting, location, profile, and notifications icon.
- Search Bar with placeholder and filter button.
- Promotional Banner with gradient and CTA.
- Food Categories using `LazyRow`.
- Popular Restaurants section with custom `ElevatedCard`.
- Recommended Food section using `Card`.
- Bottom Navigation Bar.
- Fully responsive Jetpack Compose Material 3 UI.

## Technologies Used
- Kotlin
- Jetpack Compose
- Material Design 3
- Gradle Kotlin DSL

## Project Structure
- `app/src/main/java/com/example/fooddeliveryapp/MainActivity.kt`: Contains all the composable functions and UI logic.
- `app/build.gradle.kts`: Application-level dependencies.
- `build.gradle.kts`: Project-level dependencies.

## How to Open in Android Studio
1. Open Android Studio.
2. Click on **File > Open**.
3. Navigate to `CSE225_CA2/Q1_FoodDeliveryApp` and select it.
4. Wait for Gradle sync to complete.

## How to Run
1. Connect an Android emulator or a physical device.
2. Click the **Run 'app'** button (green play icon) in the Android Studio toolbar.

## Emulator/Device Requirements
- Minimum SDK: API 24 (Android 7.0)
- Target SDK: API 36

## Important Files for Code Screenshots
- `app/src/main/java/com/example/fooddeliveryapp/MainActivity.kt` (Capture the `FoodDeliveryHomeScreen` and reusable composables like `PromotionalBanner`, `CategorySection`).

## Required Output Screenshots
- The main Home Screen showing Top Bar, Search, Promo Banner, and Categories.
- Scroll down to show Popular Restaurants and Recommended Foods.
