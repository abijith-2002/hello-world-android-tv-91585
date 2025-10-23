import org.gradle.api.GradleException

plugins {
    id("com.android.application") version "8.6.1" apply false
    kotlin("android") version "1.9.24" apply false
}

// Delegation-only task; keeps analyzers happy by having a gradle.kts file present.
tasks.register("assembleDebug") {
    doFirst { println("Delegating root :app:assembleDebug to android_tv_frontend/app") }
    doLast {
        val wrapper = file("${rootDir}/android_tv_frontend/gradlew")
        if (!wrapper.exists()) throw GradleException("Gradle wrapper not found at android_tv_frontend/gradlew")
        val proc = ProcessBuilder("bash", "-lc", "./android_tv_frontend/gradlew -p android_tv_frontend :app:assembleDebug")
            .directory(rootDir)
            .redirectErrorStream(true)
            .start()
        proc.inputStream.bufferedReader().lines().forEach { println(it) }
        val code = proc.waitFor()
        if (code != 0) throw GradleException("Delegated :app build failed with exit code $code")
    }
}
