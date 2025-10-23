#!/usr/bin/env bash
# Prints the path to the debug APK if present, otherwise builds it and prints the path.
set -euo pipefail
cd "$(dirname "$0")"
APK="app/build/outputs/apk/debug/app-debug.apk"
if [[ ! -f "$APK" ]]; then
  ./gradlew --no-daemon :app:assembleDebug
fi
if [[ -f "$APK" ]]; then
  echo "$APK"
  exit 0
else
  echo "Debug APK not found at $APK" >&2
  exit 1
fi
