#!/usr/bin/env bash
set -euo pipefail
echo "CI working directory: $(pwd)"
echo "Listing repo root:"
ls -la
echo "Attempting to build nested Android project..."
cd android_tv_frontend
chmod +x ./gradlew
./gradlew -p . :app:assembleDebug
