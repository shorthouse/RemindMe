buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.4.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.10")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.3")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.45")
    }
}

plugins {
    id("com.autonomousapps.dependency-analysis") version "1.19.0"
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
