# TermNotes

A small, completely offline Android notes app with an OLED-black background and green terminal-inspired interface.

## Features

- Create, edit, search, and delete notes
- Local persistence using Room / SQLite
- No account, network, AI, API key, cloud sync, or subscription
- Kotlin and Jetpack Compose UI
- GitHub Actions debug APK builds

## Build

Requirements: JDK 17 and Android SDK Platform 36.

On macOS/Linux, run:

```bash
./gradlew testDebugUnitTest
./gradlew lintDebug
./gradlew assembleDebug
```

On Windows, use `gradlew.bat` with the same task names.

The debug APK is created at `app/build/outputs/apk/debug/app-debug.apk`.

## Download an APK

Open the repository's **Actions** tab, select a successful **Android Build** run, and download the `TermNotes-debug-apk` artifact. Artifacts are retained for 14 days. A debug APK is for testing, not a signed public release.

## Privacy

TermNotes stores notes in an on-device Room database. The app declares no network permission and does not implement online services. Uninstalling the app or clearing its storage removes local notes.

## Toolchain

- Kotlin 2.2.21
- Android Gradle Plugin 8.13.2
- Gradle 8.13
- JDK 17
- Compile / target SDK 36
- Minimum SDK 26

Compose BOM 2026.06.01 is used to keep the Compose artifacts aligned with the API 36 / AGP 8 toolchain. Compose BOM 2026.09.00 requires a newer Android build toolchain, so it is intentionally not paired with AGP 8.13.2.
