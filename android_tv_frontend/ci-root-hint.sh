#!/usr/bin/env bash
# Helper for tooling: prints the working directory and the exact command to build the project.
# Non-interactive; safe to source or execute.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
echo "ANDROID_PROJECT_ROOT=${SCRIPT_DIR}"
echo "BUILD_COMMAND=./gradlew assembleDebug"
echo "APK_PATH=${SCRIPT_DIR}/app/build/outputs/apk/debug/app-debug.apk"
