#!/usr/bin/env bash
set -euo pipefail
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$DIR"
./gradlew assembleDebug
echo "APK at: $DIR/app/build/outputs/apk/debug/app-debug.apk"
