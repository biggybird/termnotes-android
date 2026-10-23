# TermNotes

TermNotes is a minimal Android notes application built with Kotlin and Jetpack Compose. It uses a pure-black, terminal-inspired interface with green monospace accents and stores notes locally on the device.

> **Build status:** The latest recorded GitHub Actions build succeeded on 23 October 2026. See [Build TermNotes APK](https://github.com/biggybird/termnotes-android/actions) for the current status and downloadable artifacts.

## Features

- Create, edit, search, and delete notes.
- Search note titles and note content.
- Store notes in a local Room database backed by SQLite.
- OLED-friendly black interface with green terminal-style accents.
- Reject saving a note when both title and content are blank.
- Generate a title from the first line of content when the title is left blank.
- Build a debug APK using GitHub Actions.

## Screenshots

Screenshots have not been added to this repository yet.

## Technology stack

- Kotlin
- Jetpack Compose and Material 3
- Android Gradle Plugin 8.13.2
- Gradle 8.13
- JDK 17
- Android SDK: compile/target 36, minimum 26
- Room 2.8.5 for local persistence
- Kotlin Coroutines and Flow
- JUnit 4 for unit tests

Dependency versions are managed in `gradle/libs.versions.toml`.

## Privacy and offline storage

TermNotes is designed to work offline. Notes are stored in the app's private on-device Room/SQLite database. The Android manifest does not declare the `INTERNET` permission. The project does not require an API key, account, cloud sync, or subscription.

Notes are local to the installation. **Uninstalling the app or clearing its app storage removes the saved notes.** Keep a separate copy of anything important.

## Download and install the APK

A debug APK is produced by the **Build TermNotes APK** GitHub Actions workflow. Artifacts are currently configured to expire after 14 days.

### Download on an Android phone

1. Open the repository's [Actions page](https://github.com/biggybird/termnotes-android/actions). Sign in to GitHub if the repository is private.
2. Select the latest **Build TermNotes APK** run.
3. Make sure its status is **Success**.
4. Scroll to **Artifacts** and tap **TermNotes-debug-apk**. GitHub downloads a ZIP file.
5. Open your phone's **Files** app and find the downloaded ZIP, usually in **Downloads**.
6. Extract the ZIP. Inside, locate `app-debug.apk`.
7. Tap `app-debug.apk` and follow Android's installation prompts.
8. If Android blocks the installation, open the system prompt to allow your browser or file manager to install apps from that source. Only allow this for a file you trust, then return to the installer.
9. Open **TermNotes** from your app drawer.

The exact wording of Android's security prompts varies by device and Android version. This is a debug build intended for testing, not a signed production release. You may need to uninstall a previous build signed with a different key before installing this one.

### Start a build manually

1. Open the [Actions page](https://github.com/biggybird/termnotes-android/actions).
2. Choose **Build TermNotes APK**.
3. Tap **Run workflow**, select `main`, and tap **Run workflow** again.
4. Open the new run and wait for it to finish. Download the artifact only if the run succeeds.

A push to `main` also triggers this workflow automatically.

## Build locally

### Requirements

- JDK 17
- Android SDK Platform 36 and Android build tools 36.0.0
- Internet access for downloading Gradle and Android build dependencies on the first build

### macOS or Linux

Clone the repository and enter the project directory:

```bash
git clone https://github.com/biggybird/termnotes-android.git
cd termnotes-android
```

The repository includes a `gradlew` launcher script that downloads Gradle 8.13 when needed. Make it executable if necessary, then build:

```bash
chmod +x gradlew
./gradlew :app:assembleDebug
```

To run the available local unit tests and Android lint checks separately:

```bash
./gradlew :app:testDebugUnitTest
./gradlew :app:lintDebug
```

### Windows

Open a terminal in the project directory and run:

```bat
gradlew.bat :app:assembleDebug
```

The debug APK output path is:

```text
app/build/outputs/apk/debug/app-debug.apk
```

You can copy that APK to your Android phone and install it using the steps above.

## Testing and verification status

The GitHub Actions workflow builds the debug APK and verifies that the expected APK file exists before uploading it. The latest recorded run succeeded.

The workflow does **not currently run unit tests, Android lint, emulator tests, or device tests**. A successful APK build alone does not prove every feature works at runtime. The repository contains a unit test for blank-note validation, but this test must be run separately using the command above.

No physical Android-device test is claimed here.

## Known limitations

- Screenshots are not yet available in the repository.
- Only a debug APK is generated; a signed production release is not configured.
- GitHub Actions artifacts expire after 14 days.
- Notes are stored only on the current device and are not synchronized or backed up by TermNotes.
- Automated UI, emulator, and physical-device coverage has not been established.
- The current automated workflow builds the APK but does not run tests or lint.

## Troubleshooting

### The workflow fails

1. Open the [Actions page](https://github.com/biggybird/termnotes-android/actions).
2. Open the failed **Build TermNotes APK** run.
3. Open the step marked with a red cross and read the first error message.
4. Check that the latest commit is on `main`. Fix the reported issue and push a new commit, or run the workflow again after correcting the configuration.

### No APK artifact appears

- Confirm that the workflow run finished with **Success**.
- Check that the **Upload debug APK** step succeeded.
- Artifacts are attached to an individual workflow run, not directly to the repository's Code page.
- Artifacts expire after 14 days, so start a new workflow if the artifact has expired.

### Gradle or SDK errors during a local build

- Confirm that `java -version` reports JDK 17.
- Install Android SDK Platform 36 and Build Tools 36.0.0 through Android Studio's SDK Manager.
- Ensure Android SDK environment variables or Android Studio's local SDK configuration point to the installed SDK.
- Run the build again and inspect the first Gradle error rather than only the final failure summary.

### Android will not install the APK

- Make sure the ZIP was extracted and you selected `app-debug.apk`, not the ZIP itself.
- Review Android's install-from-this-source prompt for the app used to open the APK.
- If an existing TermNotes installation conflicts because it was signed differently, uninstall the old app first. **Uninstalling removes its local notes.**

## License

No `LICENSE` file is currently present in this repository, so no license is declared here. Add a license file before redistributing the project under an open-source license.
