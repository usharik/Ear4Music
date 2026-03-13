# Ear4Music

[![Android CI](https://github.com/usharik/Ear4Music/actions/workflows/android-ci.yml/badge.svg)](https://github.com/usharik/Ear4Music/actions/workflows/android-ci.yml)
[![Build APKs](https://github.com/usharik/Ear4Music/actions/workflows/build-apks.yml/badge.svg)](https://github.com/usharik/Ear4Music/actions/workflows/build-apks.yml)

Android application for the development of musical ear.

<img width="200" alt="2018-02-26 11 46 21" src="https://user-images.githubusercontent.com/15856751/36667404-02c5b9fe-1aee-11e8-9c72-fb95ba6a83c1.png">

| Requirement | Value |
|---|---|
| Minimum Android version | Marshmallow 6.0 (API 23) |
| Target SDK | Android 15 (API 36) |

## Features

- Ear training exercises with configurable tasks and subtasks
- Interactive piano keyboard for note input
- MIDI note playback for accurate audio feedback
- Tracks progress and favourite tasks
- Banner ads integration via Google AdMob

## Technical Stack

- **Language:** Java 17
- **Architecture:** MVVM with Dagger 2 dependency injection
- **UI:** AndroidX + Material Components 1.13.0 + DataBinding
- **Edge-to-Edge:** Full Android 15 edge-to-edge support via `EdgeToEdge.enable()`
- **Database:** Room Database (with migrations)
- **Reactive:** RxJava 3 + RxAndroid
- **Lifecycle:** AndroidX Lifecycle (lifecycle-runtime, lifecycle-viewmodel, lifecycle-livedata)
- **Testing:** JUnit 5 + Espresso

## Building from Source

1. Clone the repository.
2. Open in Android Studio (Hedgehog or newer recommended).
3. Sync Gradle dependencies.
4. Run the `app` configuration on a device or emulator (API 23+).

### Manual APK Building

Use the **Build APKs** workflow to build both debug and release APKs via GitHub Actions without a local Android environment:

1. Navigate to **Actions → Build APKs** in this repository.
2. Click **Run workflow**.
3. Download the resulting APK artifacts once the run completes.

## Contributing

1. Fork the repository and create a feature branch.
2. Follow the existing Java/AndroidX code style.
3. Ensure all unit and instrumented tests pass (`./gradlew test connectedAndroidTest`).
4. Open a pull request against `master` with a clear description of the change.
