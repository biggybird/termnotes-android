#!/bin/sh
set -eu
GRADLE_VERSION="8.13"
GRADLE_HOME_DIR="${GRADLE_USER_HOME:-$HOME/.gradle}/termnotes-gradle"
GRADLE_BIN="$GRADLE_HOME_DIR/gradle-$GRADLE_VERSION/bin/gradle"
if [ ! -x "$GRADLE_BIN" ]; then
  mkdir -p "$GRADLE_HOME_DIR"
  ARCHIVE="$GRADLE_HOME_DIR/gradle-$GRADLE_VERSION-bin.zip"
  if [ ! -f "$ARCHIVE" ]; then
    curl --fail --location --retry 3 "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip" --output "$ARCHIVE"
  fi
  unzip -q -o "$ARCHIVE" -d "$GRADLE_HOME_DIR"
fi
exec "$GRADLE_BIN" "$@"
