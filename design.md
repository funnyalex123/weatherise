# App Description and Design – WeatherWise

## 1. App Overview

**Developer:** Aleksandra Sipowicz  
**Student ID:** 2607302  
**App Title:** WeatherWise  
**Module:** CM3147 – Native Mobile Apps

WeatherWise is an Android weather application designed to help users plan outdoor activities using weather forecasts. Users can search for cities, view hourly weather conditions and receive activity recommendations based on temperature, rainfall probability and wind speed.

The application addresses the coursework requirements as follows:

- **MUST:** Interactive Jetpack Compose interface with state preserved during screen rotation.
- **SHOULD:** Lists of forecasts, ViewModel and StateFlow architecture, three navigable screens, internet data and error handling.
- **COULD:** Room database for offline access, WorkManager background synchronisation and responsive layouts.

WeatherWise aims to provide practical weather information through a clear, accessible interface.

## 2. UI/UX Design and View Layer

WeatherWise uses Kotlin, Jetpack Compose and Material 3 components.

The application contains three main screens:

- **Today:** Displays the selected location, weather conditions and hourly forecasts.
- **Plan Outdoors:** Helps users assess suitable times for outdoor activities.
- **Places:** Allows users to search for cities and manage saved locations.

The interface uses cards, buttons, text fields and scrolling lists, including `LazyColumn`.

Reusable composables such as `WeatherHero`, `HourCard` and `PlaceCard` receive information and event callbacks through parameters. Business state is managed by `WeatherViewModel`, while `rememberSaveable` preserves local planning selections.

The layout adapts to screen width. On narrower screens, information is displayed vertically. Wider layouts can display related content side by side.

The design follows Material Design principles by prioritising readable information, consistent controls and clear navigation.

**Design evidence:** Screenshots of the tested Today, Plan Outdoors, Places and landscape interfaces are stored in the `ss/` folder. Conceptual wireframes are available in `docs/wireframes.svg`.

## 3. MVVM Architecture and State Management

WeatherWise follows the Model–View–ViewModel (MVVM) architecture.

- **View:** Jetpack Compose displays weather information and receives user interactions.
- **ViewModel:** `WeatherViewModel` exposes `WeatherState`, `SearchState`, saved locations and notification messages through `StateFlow`. `SavedStateHandle` preserves the selected location identifier and search query during configuration changes.
- **Model:** `WeatherRepository` coordinates communication between the Open-Meteo API and the local Room database.

The application follows unidirectional data flow. When a user selects a city, the ViewModel saves the location, requests its forecast and updates the local cache. Changes to the database are observed and reflected in the UI through `StateFlow`.

Room contains two entities:

- **Place:** ID, name, region, country, latitude, longitude and time zone.
- **CachedForecast:** Place ID, JSON forecast content and update timestamp.

The Open-Meteo JSON response contains a time zone and hourly arrays for time, temperature, precipitation probability, wind speed, weather code and daylight status.

WorkManager is configured to request periodic synchronisation of saved forecasts when network connectivity is available. Android determines the precise execution time. **Successful background execution has not yet been independently verified.**

## 4. Navigation and Resilience

WeatherWise uses three navigation destinations: `today`, `plan` and `places`. They share an activity-scoped ViewModel, allowing the selected location to remain consistent between screens.

Network and database errors are handled through visible UI messages. Previously cached forecasts remain available when refreshing fails. Older forecasts are identified so users can distinguish saved information from newly downloaded data.

Activity recommendations are guidance based on weather thresholds, not guarantees of outdoor safety.

## 5. Testing and Design Validation

WeatherWise was manually tested in Android Studio using the Medium Phone emulator with API 37.0 on 10 November 2026.

Manual testing covered navigation, weather data retrieval, user interactions, screen rotation, offline cached data and network error handling. These checks were recorded as successful.

Automated unit test execution, Room database migration testing and WorkManager background execution remain unverified.

Detailed testing results are recorded in `evidence/test-results.md`.