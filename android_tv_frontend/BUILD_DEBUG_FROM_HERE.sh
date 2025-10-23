#!/usr/bin/env bash
set -euo pipefail
# Helper for tools: run this from the project root to build the debug APK.
exec "$(dirname "$0")/gradlew" :app:assembleDebug
