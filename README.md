# TERMNOTES

> **[⬇ Download the latest TERMNOTES APK](https://github.com/biggybird/termnotes-android/releases/download/latest-apk/app-debug.apk)**  
> This direct link is updated automatically by the successful `main`-branch build. The repository is private, so you must be signed in with repository access. This is a debug APK for testing.

A native Android terminal-inspired notes app built with Kotlin, Jetpack Compose, Material 3, and Room. It works offline and stores notes on the device.

## Current features

- Create, edit, search, and delete notes with confirmation.
- Room/SQLite local persistence with creation and modification timestamps.
- Four selectable themes: Hacker Green, Amber CRT, Matrix Blue, and Retro White.
- Locally saved theme, font choice, CRT scanline, and sound preferences.
- Terminal command input with `help`, `list`, `new`, `search <text>`, `clear`, `:new`, `:theme`, `:clear`, and `:help`.
- In-memory command history using keyboard Up/Down keys where the keyboard provides them.
- Basic Markdown editor/preview for headings, emphasis markers, lists, inline code, and fenced code markers. Code is never executed.
- JSON backup export using Android's file picker.
- JSON backup import validates the backup before changes, merges notes, and skips duplicate title/body pairs.
- Optional native Compose scanline overlay and optional save confirmation beep.
- Edge-to-edge layout with native safe-drawing and keyboard insets.

## Appearance and fonts

Theme selection and font selection are saved locally. Typography includes System Monospace, System Serif, System Sans Serif, and the bundled VT323 retro terminal typeface shown in the selector as “Nothing CRT.” Its SIL Open Font License is included at `app/src/main/assets/licenses/OFL-VT323.txt`. No remote fonts are loaded.

CRT scanlines and the save sound are off by default. Scanlines use a lightweight Compose drawing layer. Save confirmation uses a short Android system notification tone after a successful note save and releases the tone generator after playback.

## Privacy and storage

- No account, API key, analytics, or cloud sync is required.
- The app manifest does not request internet access.
- Notes and preferences stay in app-private storage on the device.
- Exported backups are written only to the location selected in Android's file picker.
- Import uses a merge strategy. The entire JSON structure is validated before database insertion; existing matching title/body pairs are skipped.
- Notes are not encrypted by this app. Clearing app storage or uninstalling the app removes local notes, so export backups for anything important.

## Technology stack

- Kotlin and Jetpack Compose
- Material 3
- Room / SQLite
- Gradle Kotlin DSL
- JDK 17, Gradle 8.13, Android Gradle Plugin 8.13.2
- Android SDK 36, minimum SDK 26

Dependency versions are managed in `gradle/libs.versions.toml`.

## Build the APK with GitHub Actions

The workflow is `.github/workflows/build-apk.yml`. It runs on pushes to `main` and can be started manually with **workflow_dispatch**. It runs unit tests, Android lint, builds a debug APK with the project Gradle wrapper, checks that the APK exists, and uploads the artifact `TermNotes-debug-apk` for 14 days.

### Download on Android

1. Open the repository's [Actions page](https://github.com/biggybird/termnotes-android/actions).
2. Select the newest **Build TermNotes APK** run and wait for **Success**.
3. Open the run and download the **TermNotes-debug-apk** artifact ZIP.
4. Extract the ZIP in your Files app and tap `app-debug.apk`.
5. Follow Android's installation prompt. Only allow installation from a source you trust.

The repository is private, so sign in to the GitHub account with access. A debug APK is intended for testing and is not a signed production release.

### Build locally

Requirements: JDK 17, Android SDK Platform 36, build tools 36.0.0, and network access for first-time dependency downloads.

```bash
git clone https://github.com/biggybird/termnotes-android.git
cd termnotes-android
chmod +x ./gradlew
./gradlew :app:testDebugUnitTest
./gradlew :app:lintDebug
./gradlew :app:assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`.

## Verification notes

A successful CI APK build verifies compilation and artifact creation, but it does not prove all runtime behavior. The workflow now also runs unit tests and lint. Emulator and physical-device testing must be reported separately when performed. The Nothing CRT option uses the bundled VT323 font; other system font options use their corresponding Android system families.

## Known limitations

- The Markdown preview is intentionally lightweight and does not implement the entire Markdown specification.
- Fira Code is not bundled. The Nothing CRT option uses the bundled, licensed VT323 font.
- Command Up/Down history depends on the Android keyboard exposing arrow key events.
- No signed production release configuration or physical-device test is claimed.
