#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
WRAPPER="$ROOT/android_tv_frontend/gradlew"
if [ ! -x "$WRAPPER" ]; then
  echo "[run-android-build] ERROR: Gradle wrapper not found at android_tv_frontend/gradlew" >&2
  echo "[run-android-build] Ensure the Android project root is at android_tv_frontend/ and try again." >&2
  exit 2
fi
cd "$ROOT/android_tv_frontend"
echo "[run-android-build] Running assembleDebug..."
exec ./gradlew --no-daemon assembleDebug
