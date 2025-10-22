# Build Forwarding

Some CI/analyzers invoke `assembleDebug` at repository root. Use one of these:

Kotlin DSL init script:
- ./gradlew -I android_tv_frontend/forward-tasks.gradle.kts :app:assembleDebug

Groovy init script:
- ./gradlew -I android_tv_frontend/forward-tasks.gradle :app:assembleDebug
