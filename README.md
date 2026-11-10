# WeatherWise — Android Weather & Outdoor Planner

**Student Name:** Aleksandra Sipowicz  
**Student ID:** 2607302  
**Module:** CM3147 – Native Mobile Apps  
**Application:** WeatherWise

## Project Overview

WeatherWise is an Android weather application designed to help users search for locations, view weather forecasts and plan outdoor activities such as walking, cycling and picnics.

The application uses Kotlin, Jetpack Compose (Material 3), Room Database, WorkManager, Coroutines, StateFlow and the Open-Meteo REST API. It provides three main screens and supports offline access to previously cached weather information.

## Implemented Features

### User Interface and Interaction

- **Jetpack Compose:** Material 3 components, including cards, buttons, text fields, navigation bars and activity filters.
- **Interactive Controls:** Location search, place selection, activity preferences, manual forecast refresh and place removal.
- **State Management:** ViewModel, StateFlow, SavedStateHandle and Compose state preservation.

### Application Architecture

WeatherWise uses an MVVM-based architecture with separate UI, ViewModel, repository and data layers.

The application includes three screens:

1. **Today:** Displays current weather information, forecast summaries, hourly weather trends and a visual temperature chart.
2. **Plan Outdoors:** Helps users assess weather suitability for walking, cycling and picnics using activity filters.
3. **Places:** Allows users to search for towns and manage saved locations.

### Data and Networking

- **Open-Meteo API:** Retrieves weather forecasts and location search results.
- **Room Database:** Stores saved places and cached forecasts for offline access.
- **Coroutines and Flow:** Support asynchronous operations and reactive UI updates.
- **Error Handling:** Provides feedback for network failures, empty results and unavailable weather information.

### Additional Features

- **WorkManager:** A `ForecastWorker` is configured to request periodic background synchronisation every six hours with network constraints. Successful background execution has not yet been independently verified.
- **Adaptive Layout:** Uses responsive Compose layouts for different screen widths.
- **Weather Visualisation:** Includes an interactive 24-hour forecast chart.
- **Dark Mode:** Supports Android system light and dark themes.

## Development Environment

- **IDE:** Android Studio
- **Programming Language:** Kotlin
- **UI Framework:** Jetpack Compose with Material 3
- **Android SDK:** minSdk 26, targetSdk 36
- **Java:** JDK 17
- **Test Device:** Android Emulator — Medium Phone, API 37.0
- **Operating System Used for Development:** macOS

## Running the Application

1. Download or clone the GitHub repository.
2. Open Android Studio.
3. Select **File > Open** and choose the project folder containing `settings.gradle.kts`.
4. Allow Gradle to synchronise and install any required SDK components.
5. Select the `app` run configuration and an Android emulator.
6. Click **Run** to launch WeatherWise.

The following commands can also be used from the project directory to run automated tests and build a debug APK:

```bash
./gradlew testDebugUnitTest
./gradlew assembleDebug
```

These commands are provided for verification; their successful execution has not been independently confirmed in the submitted testing record.

## Testing and Validation

WeatherWise was launched and manually tested in Android Studio using the Medium Phone emulator with API 37.0 on 10 November 2026.

The following manual checks were recorded as successful:

- Navigation between Today, Plan Outdoors and Places — **PASS**
- Weather data retrieval and display — **PASS**
- Location search and interactive controls — **PASS**
- Screen rotation and UI state behaviour — **PASS**
- Offline access to previously cached data — **PASS**
- Network error handling — **PASS**

The following areas remain unverified:

- WorkManager background synchronisation — **NOT VERIFIED**
- Automated unit test execution — **NOT VERIFIED**
- Room database migration testing — **NOT VERIFIED**

Detailed results and limitations are documented in `evidence/test-results.md`. Application screenshots are stored in the `ss/` folder.

## Project Documentation

The repository contains the following coursework documentation:

- `design.md` — application design, architecture and implementation decisions.
- `security-review.md` — security considerations, risks and mitigations.
- `tool-review.md` — evaluation of tools used during development.
- `ai-log.md` — record and critical evaluation of AI assistance.
- `evidence/test-results.md` — manual testing evidence and limitations.
- `ss/` — screenshots of the application.

## Generative AI Usage Declaration

I acknowledge the use of ChatGPT (OpenAI, https://chatgpt.com/) to support the development of my WeatherWise Android application and coursework documentation.

ChatGPT assisted with application planning, Kotlin and Jetpack Compose code suggestions, architecture, debugging guidance, Android Studio and GitHub workflows, and documentation preparation.

I reviewed AI-generated suggestions, manually tested the application in the Android emulator and made decisions about the final implementation. I did not assume that AI-generated code or explanations were correct without verification.

Further details, including problems encountered and corrections made, are recorded in `ai-log.md`.

## References

- Open-Meteo Weather API: https://open-meteo.com/en/docs
- Open-Meteo Geocoding API: https://open-meteo.com/en/docs/geocoding-api
- Android Developers: https://developer.android.com/
- Jetpack Compose: https://developer.android.com/compose
- Room Database: https://developer.android.com/training/data-storage/room
- WorkManager: https://developer.android.com/topic/libraries/architecture/workmanager

WeatherWise was developed as coursework for CM3147 – Native Mobile Apps.