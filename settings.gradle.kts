/*
  Workspace-level settings (Kotlin DSL) to aid analyzers that assume the Gradle root is the workspace root.
  It forwards to the actual project under android_tv_frontend.
*/
rootProject.name = "workspace-root-forwarder-kts"
apply(from = "android_tv_frontend/settings.gradle.kts")
