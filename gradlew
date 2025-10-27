#!/usr/bin/env bash
# Relay Gradle wrapper for analyzers assuming the Gradle root at the workspace root.
# Forwards all commands to android_tv_frontend/gradlew.
set -euo pipefail
exec "$(dirname "$0")/android_tv_frontend/gradlew" "$@"
