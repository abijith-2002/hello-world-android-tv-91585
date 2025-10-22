#!/usr/bin/env bash
set -euo pipefail
# Build from the Android Gradle root
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$DIR"
if [ ! -x ./gradlew ]; then chmod +x ./gradlew || true; fi
./gradlew :app:assembleDebug
