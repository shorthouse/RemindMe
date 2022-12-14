![RemindMe Main](https://user-images.githubusercontent.com/73708076/207454305-b870d65b-6065-4af2-ae4c-0ae64c1e6adc.png)

# Overview
**RemindMe** is an Android app that allows you to set customised reminders, giving you more time to relax 🏖️.

The app is built using Kotlin and follows best practices for Android design and development. 

The core features of the app are fully completed, and the app is under heavy development to further improve its feature set. Currently, the app is being migrated to Jetpack Compose.

# Features
The app has the following feature set:
 - Set reminders at a specific time and date, and receive a notification when the reminder fires
 - Create repeating reminders
 - Filter, search, and sort reminders
 - Edit, complete, and delete reminders
 - Dark mode
 - Automatic updates to reminders when a timezone shift occurs

# Architecture
RemindMe utilises modern components and follows best practices for architecture, including:
- Jetpack Compose
- MVVM
- Room 
- Coroutines
- Hilt DI
- View Binding & Data Binding
- Navigation Component
- Material Design

# Testing
To facilitate testing of components, RemindMe uses the following:
 - JUnit
 - Espresso
 - Truth
 - MockK
 - UI Automator

As the app is currently being migrated to Jetpack Compose, the Jetpack Compose testing library is also being integrated and used in the test suite.

# Future Features
There are many upcoming features in the backlog for RemindMe, alongside architecture and refactoring changes.

To give a sneak peek, here are some of the upcoming changes:
 - More customisable repeating reminder durations
 - Jetpack Compose
 - Material Design 3
 - Firebase integration 
 - CI/CD pipeline
