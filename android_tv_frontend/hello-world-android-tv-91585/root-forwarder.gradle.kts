import org.gradle.api.tasks.Exec

tasks.register<Exec>("forwardAssembleDebug") {
    group = "forward"
    description = "Executes the nested android_tv_frontend Gradle wrapper to assembleDebug"
    workingDir = file("android_tv_frontend")
    commandLine = listOf("./gradlew", "assembleDebug")
}
