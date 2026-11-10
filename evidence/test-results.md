# WeatherWise verification record

**Status:** Not run in the project preparation environment. Complete these checks on your Windows Android Studio installation and record actual outcomes.

| Check | Expected | Actual result | Evidence |
|---|---|---|---|
| Gradle sync | Succeeds | NOT RUN | Add build output |
| `gradlew.bat testDebugUnitTest` | Unit tests pass | NOT RUN | Add test report |
| `gradlew.bat lintDebug assembleDebug` | Lint and debug build complete | NOT RUN | Add output |
| Emulator launch | Today, Plan outdoors, Places screens open | NOT RUN | Add real screenshots |
| Search online | City results from Open-Meteo | NOT RUN | Add screenshot |
| Offline cache | Previously fetched forecast remains available | NOT RUN | Record airplane-mode test |
| Rotation | State remains after rotating emulator | NOT RUN | Record test |
| Error handling | Network failure message appears | NOT RUN | Record test |
| Room persistence | Saved place/forecast remains after restart | NOT RUN | Record test |
| WorkManager | Worker syncs when constraints are met | NOT RUN | Record test with logs/WorkManager inspector |
| Adaptive UI | Layout changes on wider virtual device | NOT RUN | Add screenshots |

Do not mark a test PASS unless you actually observed the result.
