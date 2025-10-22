#!/usr/bin/env bash
set -euo pipefail
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
exec "$SCRIPT_DIR/gradlew" -I "$SCRIPT_DIR/gradle-init-discovery.gradle.kts" :app:assembleDebug
