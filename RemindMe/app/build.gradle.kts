@file:Suppress("UnstableApiUsage")

import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

plugins {
    id("com.android.application")
    id("dagger.hilt.android.plugin")
    id("com.google.devtools.ksp") version "1.8.10-1.0.9"
    id("com.google.protobuf") version "0.8.17"
    id("org.jlleitschuh.gradle.ktlint") version "11.3.1"
    id("io.gitlab.arturbosch.detekt") version "1.22.0"
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization") version "1.8.10"
}

android {
    namespace = "dev.shorthouse.remindme"
    compileSdk = 33

    defaultConfig {
        applicationId = "dev.shorthouse.remindme"
        minSdk = 26
        targetSdk = 33
        versionCode = 17
        versionName = "2.1.4"
        testInstrumentationRunner = "dev.shorthouse.remindme.runner.CustomTestRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    kotlin {
        sourceSets {
            debug {
                kotlin.srcDir("build/generated/ksp/debug/kotlin")
            }
            release {
                kotlin.srcDir("build/generated/ksp/release/kotlin")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.2"
    }
    testOptions {
        animationsDisabled = true
    }
}

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    version.set("11.3.2")
    ignoreFailures.set(false)
    android.set(true)
}

kapt {
    correctErrorTypes = true
}

dependencies {
    // Core
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.compose.ui:ui-tooling-preview:1.4.3")

    // Compose
    val composeBom = platform("androidx.compose:compose-bom:2023.04.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation("androidx.compose.material3:material3:1.1.0-rc01")
    implementation("androidx.compose.material:material-icons-extended:1.4.3")
    implementation("com.google.accompanist:accompanist-permissions:0.30.0")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.30.0")
    implementation("io.github.raamcosta.compose-destinations:animations-core:1.8.38-beta")
    debugImplementation("androidx.compose.ui:ui-tooling:1.4.3")
    ksp("io.github.raamcosta.compose-destinations:ksp:1.8.38-beta")

    // Firebase
    val firebaseBom = platform("com.google.firebase:firebase-bom:31.3.0")
    implementation(firebaseBom)
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-perf-ktx")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-testing:2.6.1")

    // Room
    implementation("androidx.room:room-runtime:2.5.1")
    implementation("androidx.room:room-ktx:2.5.1")
    kapt("androidx.room:room-compiler:2.5.1")

    // Proto Datastore
    implementation("androidx.datastore:datastore:1.0.0")
    implementation("com.google.protobuf:protobuf-javalite:3.22.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.8.1")
    implementation("androidx.hilt:hilt-work:1.0.0")
    kapt("androidx.hilt:hilt-compiler:1.0.0")

    // GSON
    implementation("com.google.code.gson:gson:2.10.1")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.45")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    kapt("com.google.dagger:hilt-android-compiler:2.45")

    // Local unit tests
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.test.ext:junit-ktx:1.1.5")
    testImplementation("org.robolectric:robolectric:4.9.2")
    testImplementation("com.google.truth:truth:1.1.3")
    testImplementation("io.mockk:mockk:1.13.4")
    testImplementation("org.mockito:mockito-core:5.2.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")

    // Instrumentation tests
    androidTestImplementation("androidx.test:core-ktx:1.5.0")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("android.arch.core:core-testing:1.1.1")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("com.android.support.test.espresso:espresso-contrib:3.0.2")
    androidTestImplementation("androidx.navigation:navigation-testing:2.5.3")
    androidTestImplementation("com.google.truth:truth:1.1.3")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    androidTestImplementation("org.mockito:mockito-android:5.2.0")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.45")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.4.3")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:2.45")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.14.0"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}
