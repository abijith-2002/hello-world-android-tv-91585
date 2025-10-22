rootProject.name = "android-tv-hello-world-root-proxy-kts"

// Delegate to the real project in android_tv_frontend when invoked from repo root.
includeBuild("android_tv_frontend")
