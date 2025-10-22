tasks.register("assembleDebug") {
    group = "build"
    description = "Forwarded task for analyzers; call :app:assembleDebug"
    dependsOn(gradle.includedBuilds.flatMap { it.taskNames })
    doLast {
        println("Use explicit path: ./gradlew :app:assembleDebug")
    }
}
