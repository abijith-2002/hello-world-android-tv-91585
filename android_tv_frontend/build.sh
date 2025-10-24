#!/bin/bash

# ----------------------------
# Android Build Script
# ----------------------------

# Exit on first error
set -e

# Set Android SDK path (change if needed)
export ANDROID_HOME=/opt/android-sdk-linux
export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools

# Function to print usage
usage() {
  echo "Usage: $0 [--clean]"
  echo "  --clean    Clean the project before building"
  exit 1
}

# Parse argument
CLEAN=false
if [[ "$1" == "--clean" ]]; then
  CLEAN=true
elif [[ -n "$1" ]]; then
  usage
fi

# Go to the script's directory (project root)
cd "$(dirname "$0")"

# Check if gradlew exists
if [[ ! -f "./gradlew" ]]; then
  echo "❌ gradlew not found! Run this script from the project root."
  exit 1
fi

# Clean if requested
if $CLEAN; then
  echo "🧹 Cleaning project..."
  ./gradlew clean
fi

# Build debug APK
echo "⚙️  Building debug APK..."
./gradlew assembleDebug

# Locate APK output
APK_PATH=$(find ./app/build/outputs/apk/debug -name "*.apk" 2>/dev/null | head -n 1)

if [[ -f "$APK_PATH" ]]; then
  echo "✅ Build successful!"
  echo "📦 APK generated at: $APK_PATH"
else
  echo "❌ Build failed or APK not found."
  exit 1
fi

