#!/usr/bin/env bash
set -euo pipefail
# Build nested Android project
cd "$(dirname "$0")/android_tv_frontend"
chmod +x ./gradlew || true
./gradlew :app:assembleDebug
echo "Built APK: $(pwd)/app/build/outputs/apk/debug/app-debug.apk"
