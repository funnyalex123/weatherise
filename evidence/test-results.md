# WeatherWise – Test Results

## Testing Environment

- **Application:** WeatherWise
- **Platform:** Android Emulator (Android Studio)
- **Test date:** 10/11/2026
- **Device / Android version:** Medium Phone API 37.0
- **Testing method:** Manual functional testing

## Manual Test Results

| Test | Expected Result | Status |
|---|---|---|
| Navigation | User can navigate between Today, Plan Outdoors and Places screens. | PASS |
| Internet weather data | Weather forecasts are retrieved from the Open-Meteo API and displayed in the application. | PASS |
| Interactivity | Search, place selection and other interactive controls respond to user actions. | PASS |
| Screen rotation | The application preserves relevant state when the emulator rotates. | PASS |
| Offline mode and Room | Previously cached weather information remains accessible without an internet connection. | PASS |
| Error handling | The application handles unavailable network data and provides appropriate feedback. | PASS |
| WorkManager background synchronisation | Background refresh runs successfully under the expected conditions. | NOT VERIFIED |
| Automated unit tests | Gradle unit tests execute successfully. | NOT VERIFIED |
| Database migration testing | Existing saved data remains intact after a database version upgrade. | NOT VERIFIED |

## Screenshot Evidence

The `ss/` directory contains screenshots captured from the Android emulator showing:

- Today – weather forecast
- Plan Outdoors – outdoor activity recommendation
- Places – location search
- Landscape view – screen rotation and state preservation

These screenshots provide supporting evidence of the application interface and selected functionality. They do not independently prove background synchronisation or automated test execution.

## Limitations

The confirmed PASS results are based on manual testing in the Android emulator. WorkManager execution, automated unit tests and database migration behaviour have not yet been independently verified.

Further testing would be required before claiming full production readiness.