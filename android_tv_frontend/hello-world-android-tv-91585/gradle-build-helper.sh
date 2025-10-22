#!/usr/bin/env bash
set -euo pipefail
# Helper to build the nested Android project from the workspace root.
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR/android_tv_frontend"
./gradlew assembleDebug
