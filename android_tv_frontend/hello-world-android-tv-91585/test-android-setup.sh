#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT/android_tv_frontend"
echo "[test-android-setup] Running Gradle help to validate root..."
./gradlew --no-daemon help
echo "[test-android-setup] OK"
