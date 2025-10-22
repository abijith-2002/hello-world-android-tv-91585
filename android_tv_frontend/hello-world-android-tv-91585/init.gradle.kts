allprojects {
    logger.lifecycle("[init.gradle.kts] Delegating to nested Android project at android_tv_frontend/")
}
gradle.settingsEvaluated {
    val nested = file("android_tv_frontend/settings.gradle")
    if (nested.exists()) {
        apply(from = nested)
        logger.lifecycle("[init.gradle.kts] Applied nested settings.gradle")
    } else {
        logger.error("[init.gradle.kts] Nested settings.gradle not found at android_tv_frontend/settings.gradle")
    }
}
