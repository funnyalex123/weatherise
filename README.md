# WeatherWise — Android Weather & Outdoor Planner

**Coursework Module:** CM3147 — Native Mobile Apps  
**Stack:** Kotlin, Jetpack Compose (Material 3), Room Database, WorkManager, Coroutines & Flow, Retrofit/HttpURLConnection, Open-Meteo REST API.

WeatherWise is a modern, privacy-first Android application designed to help users search locations, view hourly forecasts, plan outdoor activities (walking, cycling, picnics), and maintain offline access to saved locations.

---

## 🌟 Grade A Rubric Compliance

The source code **attempts** the Must, Should and three Could areas. These claims must be validated by compiling, running and testing the application; a grade is not guaranteed.

### 1. Must Have Requirements (Grade D Baseline)
- **Basic UI:** Built using Jetpack Compose with Material 3 design tokens (`Scaffold`, `TopAppBar`, `Card`, `Button`, `OutlinedTextField`, `FilterChip`, `Switch`).
- **Basic Interactivity:** Interactive town search, place selection, activity filtering, manual forecast refresh, and place removal dialogs.
- **State Management:** Preserves state across screen rotations and process death via `WeatherViewModel` using `SavedStateHandle` and Compose `rememberSaveable`.

### 2. Should Have Requirements (Grade B Tier)
- **Lists in UI:** Uses `LazyColumn` and `LazyRow` to render dynamic hourly forecasts and saved location lists.
- **Architecture Components:** Strictly follows Modern Android Architecture (MVVM) with `AndroidViewModel`, immutable UI states (`WeatherState`, `SearchState`), `StateFlow`, and `collectAsStateWithLifecycle()`.
- **Multi-Screen Navigation:** Implements `NavHost` with `NavigationBar` managing 3 distinct screens:
  1. **Today:** High-level daily outlook, hero card, 24-hour visual trend chart, and upcoming forecast hours.
  2. **Plan Outdoors:** Outdoor suitability analysis with activity chips (Walk, Cycle, Picnic) and strict preference filters.
  3. **Places:** Town search via geocoding API and saved location management.
- **Internet Data:** Asynchronously queries the Open-Meteo REST API (`geocoding-api.open-meteo.com` and `api.open-meteo.com`) on `Dispatchers.IO`.
- **Error Handling:** Gracefully handles network failures, empty search results, and missing forecast data with user-friendly error banners (`Message`) and `SnackbarHost`.

### 3. Could Have Requirements (Grade A Distinction)
- **Requirement 6 — Room Database Persistence:** Full local offline persistence via `WeatherDatabase`, `WeatherDao`, `@Entity Place`, and `@Entity CachedForecast`. When offline, the app seamlessly loads saved forecasts from Room.
- **Requirement 7 — WorkManager Background Synchronization:** `ForecastWorker` runs a periodic background sync every 6 hours (`PeriodicWorkRequestBuilder`) with network constraints to automatically refresh saved forecasts in the background.
- **Requirement 8 — Advanced Features & Independent Research:**
  - **Adaptive Multi-Pane Layout:** Supports foldables and tablets via `BoxWithConstraints` (`maxWidth >= 700.dp`), rendering side-by-side hero and planner panes.
  - **Interactive 24-Hour Trend Chart:** Custom visual timeline (`ForecastChart`) displaying temperature trends and rain probability bars with suitability highlights.
  - **Dynamic Theme & Dark Mode:** Auto-adapts to Android system dark mode and light mode preferences.

---

## 🛠️ Build & Testing Instructions

### Requirements
- **IDE:** Android Studio with Gradle/AGP support for the versions in the project
- **SDK:** Android SDK 36 (minSdk 26, targetSdk 36)
- **JDK:** Java 17

### Run Application
```bash
# Run unit tests
./gradlew testDebugUnitTest

# Assemble debug APK
./gradlew assembleDebug
```
Output APK location: `app/build/outputs/apk/debug/app-debug.apk`

---

## 🧪 Validation & Testing Results

- **Unit tests provided, not executed in this preparation environment** (`DomainTest`). Intended coverage:
  - Weather condition mapping (`condition`).
  - JSON payload parsing (`parseForecast`).
  - Activity suitability calculations across boundaries (`suitable`).
  - DST time zone formatting across daylight savings (`formatTime`).
  - Bounded 24-hour upcoming time window filtering (`upcoming`).
- **Build Status:** NOT VERIFIED. Run `gradlew.bat assembleDebug` on Windows; resolve any reported errors.
- **Device Verification:** NOT VERIFIED. Launch and capture evidence on your Android Studio emulator.


## Open in Android Studio (Windows)
1. Extract the ZIP, choose **File > Open**, and select the `WeatherWise` folder containing `settings.gradle.kts`.
2. Allow Gradle sync and install Android SDK Platform 36 if prompted. Set Gradle JDK to 17 if needed.
3. Select the `app` run configuration and an Android emulator, then click Run.
4. If Run is missing, read the Build/Sync error first. In the terminal, try `gradlew.bat assembleDebug`.
5. Record real results in `evidence/test-results.md`. The project has not been independently built or emulator-tested in this preparation environment.

## Coursework submission steps
- Replace `[Your Name]` and `[Your ID]` placeholders in documentation and provide your actual student details.
- Check `design.md`, `security-review.md`, `tool-review.md`, and `ai-log.md` against what you really implemented and tested.
- Add two **genuine** examples of AI mistakes you found and corrected; do not invent experiences.
- Check code citations and API attribution. Capture real UI screenshots/wireframes and test evidence.
- Create your own GitHub repository, commit changes over time, add the module coordinator, push the final branch, then submit a GitHub-downloaded ZIP and URL to Moodle.
- Before a public Play Store release, replace the sample application ID `com.example.weatherwise`, review the privacy policy and Play Console requirements.
