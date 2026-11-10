# Tool Review – WeatherWise

## Development and Testing Tools

Android Studio was the main development environment for WeatherWise. I used it to work with Kotlin, Jetpack Compose, Gradle and the Android Emulator. The emulator allowed me to test navigation, weather searches, activity recommendations, screen rotation, offline functionality and error handling.

Jetpack Compose helped me create reusable interface components, while ViewModel and StateFlow supported structured state management. Room provided local storage for saved places and cached forecasts. The Open-Meteo API enabled weather data retrieval. WorkManager was included for periodic background synchronisation, although successful background execution has not yet been independently verified.

I used Git and GitHub Desktop for version control and GitHub for storing and submitting the project. During development, I committed and pushed changes to the source code and coursework documentation. GitHub Desktop was particularly useful because it provided a graphical interface for reviewing changes and synchronising the local repository with GitHub.

## Generative AI Usage and Critical Reflection

I used ChatGPT to assist with project structure, code suggestions, debugging guidance, Android Studio and GitHub workflows, and documentation. AI-generated material required human review rather than automatic acceptance.

### Example 1 – Incomplete Project Configuration

An AI-assisted project package initially lacked essential Gradle configuration files. This prevented the project from being recognised and built as expected.

I reviewed the project structure and used AI guidance to reconstruct the missing configuration. The corrected project was subsequently opened and launched in the Android Studio emulator.

This experience demonstrated that generated project files must be checked for completeness and tested in the actual development environment.

### Example 2 – Unsupported Testing Claims

AI-generated documentation initially included statements suggesting that builds and tests had succeeded before those results were independently verified.

I identified these unsupported claims and corrected the documentation to distinguish between implemented features and confirmed test results.

I then performed manual functional testing in the Android emulator, including navigation, internet weather retrieval, screen rotation, Room caching and error handling.

Automated unit tests, Room database migration tests and WorkManager background execution remain unverified.

This experience demonstrated that AI-generated explanations and documentation should not be accepted without supporting evidence.

## Lessons Learned

In future projects, I would establish version control earlier, test smaller changes more frequently and record test evidence throughout development.

I would also verify AI-generated code and suggestions against official documentation, inspect project configuration before attempting to build, and avoid claiming that functionality has been tested without evidence.

Overall, ChatGPT was useful for accelerating development and explaining unfamiliar tools, but Android Studio, the emulator, GitHub and my own testing were necessary to assess the final application.