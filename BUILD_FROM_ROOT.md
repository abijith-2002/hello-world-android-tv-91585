Building from workspace root

If your tooling requires running from the workspace root and cannot detect the Android project root automatically, use the proxy script:

- ./gradlew-proxy.sh assembleDebug

This proxies to:
- android_tv_frontend/gradlew assembleDebug

APK output:
- android_tv_frontend/app/build/outputs/apk/debug/app-debug.apk
