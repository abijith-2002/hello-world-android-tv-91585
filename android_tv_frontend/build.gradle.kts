/** PROJECT_ROOT_MARKER:HINT
 - This is the Gradle root build file (KTS) for the Android TV app.
 - Wrapper: ./gradlew
 - Modules: :app, :list, :utilities
**/
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.6.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.24")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
