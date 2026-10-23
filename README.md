# TermNotes

A minimal, offline Android notes app with a terminal-inspired OLED-black interface and green monospace accents.

## Features

- Create, edit, search and delete notes
- Saves notes locally on your device
- Terminal-inspired commands: `help`, `list`, `new`, `search <text>`, `clear`
- No account, API key, internet permission or cloud sync required

## Build an APK on GitHub

1. Open the **Actions** tab in this repository.
2. Select **Build TermNotes APK**.
3. Tap **Run workflow** and confirm.
4. Open the completed workflow run.
5. Under **Artifacts**, download **TermNotes-debug-apk**.
6. Extract the downloaded ZIP and install `app-debug.apk` on your Android phone. Android may ask you to allow installation from your browser or file manager.

A build also starts automatically whenever changes are pushed to `main`.

## Build locally

Requires JDK 17 and Gradle 8.9. Run:

```bash
gradle assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`

## Privacy

Notes are stored locally in app preferences on your device. The app is designed to work offline.
