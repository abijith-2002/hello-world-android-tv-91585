#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT/android_tv_frontend"
if [ ! -x ./gradlew ]; then chmod +x ./gradlew || true; fi
./gradlew -v
./gradlew :app:assembleDebug
