#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT/android_tv_frontend"
echo "[ci-build-android] Invoking Gradle wrapper in $(pwd)"
./gradlew --no-daemon --stacktrace assembleDebug
code=$?
if [ $code -eq 0 ]; then
  echo "[ci-build-android] Build succeeded."
  echo "[ci-build-android] APK: $(pwd)/app/build/outputs/apk/debug/app-debug.apk"
else
  echo "[ci-build-android] Build failed with code $code"
fi
exit $code
