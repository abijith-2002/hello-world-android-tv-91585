#!/usr/bin/env bash
set -euo pipefail
# CI build proxy for nested Android project
cd "$(dirname "$0")/android_tv_frontend"
exec ./gradlew :app:assembleDebug
