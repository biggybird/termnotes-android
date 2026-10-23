# TERMNOTES

**A native, offline-first notes app with a retro terminal soul.**  
Built with Kotlin, Jetpack Compose, Material 3, and Room. Your notes stay on your device.

<p align="center">
  <a href="https://github.com/biggybird/termnotes-android/releases/download/latest-apk/app-debug.apk"><strong>⬇ Download the latest debug APK</strong></a>
  ·
  <a href="https://github.com/biggybird/termnotes-android/actions">Build history</a>
  ·
  <a href="https://github.com/biggybird/termnotes-android/issues">Report a bug</a>
</p>

[![Build TERMNOTES APK](https://github.com/biggybird/termnotes-android/actions/workflows/build-apk.yml/badge.svg)](https://github.com/biggybird/termnotes-android/actions/workflows/build-apk.yml)

> **APK note:** The download above is intended to point to the latest successful main-branch debug build. If it is temporarily unavailable, open the [Actions page](https://github.com/biggybird/termnotes-android/actions), choose the newest successful run, and download its `TermNotes-debug-apk` artifact. The repository is public, so you do not need repository access to browse the source. A debug APK is for testing, not a production release.

## ✨ Features

- **Local notes:** Create, view, edit, search, and delete notes.
- **Offline by design:** Notes and preferences are stored locally with Room/SQLite.
- **Four themes:** Hacker Green, Amber CRT, Matrix Blue, and Retro White.
- **Font choices:** System Monospace, System Serif, System Sans Serif, and **Nothing CRT**, a bundled retro terminal typeface based on VT323.
- **CRT scanlines:** Optional subtle scanline overlay.
- **Save feedback:** Optional short confirmation tone after a successful save.
- **Terminal commands:** `help`, `list`, `new`, `search <text>`, `clear`, `:new`, `:theme`, `:clear`, and `:help`.
- **Command history:** Use Up/Down where the Android keyboard exposes those key events.
- **Markdown preview:** Lightweight support for common headings, emphasis, lists, inline code, and fenced code markers. Code is displayed, never executed.
- **JSON backups:** Export notes through Android's file picker and merge-import validated backups.
- **Native Android UI:** Compose layouts, system insets, and keyboard-aware behavior.

## 📱 Download and install

1. Tap **Download the latest debug APK** at the top of this page.
2. If the direct link is not available yet, use the [GitHub Actions builds](https://github.com/biggybird/termnotes-android/actions) and open the newest run marked **Success**.
3. Download the `TermNotes-debug-apk` artifact ZIP and extract it.
4. Open `app-debug.apk` on your Android device and follow the installation prompt.
5. Android may ask you to allow installation from your browser or file manager. Only install APKs you trust.

The APK produced by this workflow is a **debug build**. It is not a signed production release.

## 🛠 Tech stack

- Kotlin
- Jetpack Compose and Material 3
- Room / SQLite
- Gradle Kotlin DSL
- JDK 17
- Gradle 8.13
- Android Gradle Plugin 8.13.2
- Android SDK 36
- Minimum SDK: 26

Dependency versions are managed in `gradle/libs.versions.toml`.

## 🧑‍💻 Build locally

### Requirements

Install JDK 17 and Android SDK Platform 36 with Android Build Tools 36.0.0. The first build needs network access to download Gradle and dependencies.

### Commands

```bash
git clone https://github.com/biggybird/termnotes-android.git
cd termnotes-android
chmod +x ./gradlew
./gradlew :app:testDebugUnitTest
./gradlew :app:lintDebug
./gradlew :app:assembleDebug
```

The debug APK is generated at:

```
app/build/outputs/apk/debug/app-debug.apk
```

## ⚙️ Continuous integration

The workflow lives at [`.github/workflows/build-apk.yml`](https://github.com/biggybird/termnotes-android/blob/main/.github/workflows/build-apk.yml). It runs on pushes to `main` and supports manual runs. It runs unit tests, Android lint, assembles the debug APK, verifies that the APK exists, and uploads the `TermNotes-debug-apk` artifact.

The workflow also attempts to publish a rolling download at the direct APK link above after a successful build. Check the [latest workflow run](https://github.com/biggybird/termnotes-android/actions) if a release asset has not appeared.

## 🔐 Privacy and data

- No account, API key, analytics, or cloud sync is required by the app.
- The app is designed to work offline; it does not require internet access for note-taking.
- Notes and preferences are kept in app-private storage on the device.
- Backups are written only to the location you choose in Android's file picker.
- Import uses a merge strategy, validates the JSON structure, and skips duplicate title/body pairs.
- **Notes are not encrypted by this app.** Clearing app storage or uninstalling the app removes local notes. Export a backup before removing the app or clearing its data.

## 🔤 Fonts and attribution

The **Nothing CRT** selector label is a visual style name, not an official Nothing Phone font. It uses the bundled VT323 retro terminal typeface, distributed under the SIL Open Font License. The license and attribution are included at `app/src/main/assets/licenses/OFL-VT323.txt`. The app does not download fonts at runtime.

## ℹ️ Limitations

- Markdown support is intentionally lightweight and does not cover the full Markdown specification.
- Fira Code is not bundled.
- Command history arrow keys depend on keyboard event support.
- CI checks compilation, tests, lint, and APK packaging; emulator and physical-device behavior should be verified separately.

## 🤝 Contributing

Issues and pull requests are welcome. If something breaks, please include your Android version, device model, steps to reproduce, and relevant logs, while avoiding private note contents.

---

Made for people who like their notes local and their terminals green. 💚
