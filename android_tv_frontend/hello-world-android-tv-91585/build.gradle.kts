plugins {
    // No plugins; this is a forwarding root for includeBuild("android_tv_frontend")
}

tasks.register("assembleDebug") {
    group = "build"
    description = "Forwards to nested android_tv_frontend :app:assembleDebug"
    doLast {
        println("Forwarding assembleDebug to nested project...")
        // We rely on the included build; instruct users/CI to call gradle with the explicit task path.
        println("Run: ./gradlew :app:assembleDebug")
    }
}
