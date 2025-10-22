tasks.register("androidProbe") {
    group = "help"
    description = "Probe task to help analyzers discover the nested Android project."
    doLast {
        println("Android Gradle root is nested under: android_tv_frontend")
        println("Use: ./gradlew :app:assembleDebug")
    }
}
