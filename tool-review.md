Tool Review – WeatherWise

Development and Testing Tools

Android Studio was the main development environment for WeatherWise. I used it to work with Kotlin, Jetpack Compose, Gradle and the Android Emulator. The emulator allowed me to test navigation, weather searches, activity recommendations, screen rotation, offline functionality and error handling.

Jetpack Compose helped me create reusable interface components, while ViewModel and StateFlow supported structured state management. Room provided local storage for saved places and cached forecasts. Retrofit and the Open-Meteo API enabled weather data retrieval. WorkManager was implemented for periodic background synchronisation, although its execution has not yet been independently verified.

Git and GitHub are intended for version control and submission. Maintaining meaningful commits will help document changes and make the development process easier to review.

Generative AI Usage and Critical Reflection

I used ChatGPT to assist with project structure, code suggestions, debugging guidance and documentation. AI-generated material required human review rather than automatic acceptance.

One issue involved an incomplete Android Studio project package that lacked essential Gradle configuration files. This highlighted the importance of checking project structure and verifying that Android Studio could recognise the application. A corrected project was subsequently opened and run in the emulator.

A second issue involved documentation that claimed successful builds and tests before those results had been verified. These statements needed correction. I then performed functional testing in the emulator, including navigation, internet requests, screen rotation, Room caching and error handling.

These experiences demonstrated that AI can accelerate development but cannot replace compilation, testing or technical judgement.

Lessons Learned

In future projects, I would establish version control earlier, test smaller changes more frequently and record test evidence throughout development. I would also verify external code and AI suggestions against official documentation and applicable licences before incorporating them into a release.