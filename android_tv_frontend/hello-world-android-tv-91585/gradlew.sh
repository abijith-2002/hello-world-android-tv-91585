#!/usr/bin/env sh
# POSIX-compliant proxy to nested Gradle wrapper
set -eu
cd "$(dirname "$0")/android_tv_frontend"
exec ./gradlew "$@"
