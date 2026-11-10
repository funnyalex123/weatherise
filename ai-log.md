# AI interaction record — 3 November 2026
This is a summary, not a complete chat transcript. Export the complete conversation and link it here before submission. AI logs are excluded from the brief’s word limit.

## User prompts (verbatim)
- “build an app if you have any app in mind”
- “give me different app idea, something about weather maybe?”
- “great, that's what I need”

## Work produced
An initial PantryPal scaffold was abandoned when the user selected a weather concept. It is not included in this project. ChatGPT proposed WeatherWise, then generated Kotlin/Compose screens, Room/API/repository code, a ViewModel, a WorkManager worker, activity rules, unit test source, build configuration, a CI workflow and documentation.

Open-Meteo documentation was consulted for API fields, time handling, geocoding and licensing. Android documentation informed build versions and the source-level security review. The initial native build attempt failed before Gradle started: `java: error while loading shared libraries: libjli.so`. No compile/test/emulator success is claimed.

## Human follow-up required
Review every file, run the checks, capture actual errors/fixes, record design decisions accepted/rejected, replace identity placeholders and add genuine tool reflection. This record does not substitute for the required full interaction log or two real AI error examples.

## References
- Open-Meteo (2026), https://open-meteo.com/en/docs
- Open-Meteo (2026), https://open-meteo.com/en/docs/geocoding-api
- Open-Meteo (2026), https://open-meteo.com/en/terms
- Android Developers (2026), https://developer.android.com/build/releases/agp-8-13-0-release-notes
- Android Developers (2026), https://developer.android.com/google/play/requirements/target-sdk
- Android Developers (2026), https://developer.android.com/develop/background-work/background-tasks/persistent-work
All accessed 3 November 2026. Source code is AI-generated, not copied from these pages. Bundled Gradle wrapper JAR: Gradle 8.13.0, Apache-2.0, https://github.com/gradle/gradle/tree/v8.13.0.


## 3 November 2026 — follow-up project repair
AI assisted with reconstruction of missing Gradle root configuration and wrapper launch scripts, added a verification checklist, and corrected unsupported test/build claims in the README. These changes were not compiled or tested in this environment. The student must inspect and test them. The coursework asks for two real examples of AI errors diagnosed and corrected by the student; record only examples that genuinely occurred and how you validated each fix.


## 10 November 2026 — Emulator testing, documentation and GitHub

ChatGPT assisted me with understanding Android Studio, locating project files, using Git commands, configuring GitHub Desktop, and preparing coursework documentation.

I ran WeatherWise in the Android Studio emulator and manually verified navigation, internet weather retrieval, user interactivity, screen rotation, offline access to cached data using Room, and error handling.

I captured screenshots of the Today, Plan Outdoors, Places and landscape interfaces. I also updated the README with my student information and an AI usage declaration, and recorded the confirmed test results in `evidence/test-results.md`.

During the GitHub submission process, I encountered difficulties with command-line authentication. ChatGPT suggested using GitHub CLI, but it was not installed. I then used GitHub Desktop to authenticate through the browser and push the project to the remote repository.

I also discovered that I was editing documentation in a different local copy of WeatherWise from the folder connected to GitHub. I corrected this by copying the updated README into the repository folder and committing and pushing the changes.

Automated unit tests, database migration tests and WorkManager background execution have not yet been independently verified.

## AI interaction records

Full conversation transcript or share links: https://chatgpt.com/share/6af3299b-b058-83eb-97e8-8c9efcc21178.

## Critical Evaluation of AI Assistance

### Example 1 — GitHub Authentication

**AI suggestion:** ChatGPT recommended using GitHub CLI (`gh auth login`) to authenticate securely and push the project to GitHub.

**Problem identified:** When I ran `gh --version`, the terminal returned `command not found`. I also checked Homebrew, which was not installed. The suggested workflow therefore could not be followed on my computer without installing additional software.

**Correction:** I used GitHub Desktop instead, signed in through the browser, added my existing local repository and pushed the committed changes.

**Verification:** I opened the remote GitHub repository and confirmed that the WeatherWise source files, documentation and screenshots were visible.

**Reflection:** This demonstrated that AI instructions must be checked against the actual development environment. A technically valid suggestion may still be unsuitable for the available tools.

### Example 2 — Editing the Wrong Project Folder

**AI suggestion:** ChatGPT guided me through updating the student details and AI declaration in `README.md` using Android Studio.

**Problem identified:** Android Studio was displaying a project named `WeatherWise 3`, while the GitHub-connected repository was located in a separate folder named `weatherise`. The changes made in the first folder did not automatically appear in the GitHub repository.

**Correction:** I opened the correct repository folder through GitHub Desktop, copied the updated README content into its `README.md` file, and committed and pushed the change.

**Verification:** I checked the README in the GitHub repository and confirmed that the updated content was present.

**Reflection:** This showed the importance of verifying file paths and repository locations rather than assuming that two similarly named project folders contain the same files.