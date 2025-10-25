#!/usr/bin/env sh

# Minimal Gradle wrapper script for CI analysis
DIR="$(cd "$(dirname "$0")" && pwd)"
JAVA_OPTS=${JAVA_OPTS:-"-Xmx1024m"}
GRADLEW="$DIR/android_tv_frontend/gradlew"

if [ -x "$GRADLEW" ]; then
  exec "$GRADLEW" "$@"
else
  echo "Gradle wrapper not found in android_tv_frontend. Attempting to run gradle if installed..."
  exec gradle "$@"
fi
