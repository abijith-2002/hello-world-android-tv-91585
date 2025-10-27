#!/usr/bin/env bash
# Helper to build the Android TV project from the workspace root.
set -euo pipefail
cd "$(dirname "$0")/android_tv_frontend"
./gradlew assembleDebug
