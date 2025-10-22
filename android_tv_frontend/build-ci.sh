#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
chmod +x ./gradlew || true
./gradlew :app:assembleDebug
echo "APK: $(pwd)/app/build/outputs/apk/debug/app-debug.apk"
