#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/android_tv_frontend"
./gradlew -v >/dev/null
echo "OK: android_tv_frontend is the Android Gradle root"
