#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/android_tv_frontend"
exec ./gradlew :app:assembleDebug
