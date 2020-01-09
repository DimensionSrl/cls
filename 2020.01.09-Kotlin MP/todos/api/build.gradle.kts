import org.gradle.kotlin.dsl.execution.text
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    kotlin("android.extensions")
    id("com.android.library")
    id("kotlinx-serialization")
}

android {
    val androidCompileSdk: String by project
    val androidMinSdk: String by project
    val androidTargetSdk: String by project
    val mobileVersionCode: String by project
    val mobileVersionName: String by project

    compileSdkVersion(androidCompileSdk.toInt())
    defaultConfig {
        minSdkVersion(androidMinSdk.toInt())
        targetSdkVersion(androidTargetSdk.toInt())
        versionCode = mobileVersionCode.toInt()
        versionName = mobileVersionName
    }
    sourceSets.forEach {
        val root = "src/androidMain/${it.name}"
        it.setRoot(root)
        it.java.srcDirs("$root/kotlin")
        it.manifest.srcFile("src/androidMain/AndroidManifest.xml")
    }
    androidExtensions {
        isExperimental = true
    }
}

kotlin {
    val kotlinVersion: String by project
    val serializationVersion: String by project
    val coroutinesVersion: String by project
    val ktorVersion: String by project

    jvm()
    js()
    android()

    val ios =
        if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true) { name: String, configure: KotlinNativeTarget.() -> Unit ->
            iosArm64(name, configure)
        }
        else { name: String, configure: KotlinNativeTarget.() -> Unit ->
            iosX64(name, configure)
        }

    ios("ios") {
        binaries.framework {
            baseName = "TDSApi"
        }
        val main by compilations.getting {
            // TODO migrate due depracation
            extraOpts("-module-name", "TDS")
        }
    }

    sourceSets {
        all {
            languageSettings.useExperimentalAnnotation("kotlin.Experimental")
        }
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:$serializationVersion")

                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-logging:$ktorVersion")
                implementation("io.ktor:ktor-client-json:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")

                implementation(project(":data"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))

                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization-jvm:$ktorVersion")

                implementation("org.slf4j:slf4j-simple:1.6.4")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-json-js:$ktorVersion")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:$serializationVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")

                implementation("io.ktor:ktor-client-android:$ktorVersion")
                implementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
                implementation("io.ktor:ktor-client-logging-jvm:$ktorVersion")
                implementation("io.ktor:ktor-client-json-jvm:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization-jvm:$ktorVersion")

                implementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
                implementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
            }
        }
        val iosMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-native:$coroutinesVersion")

                implementation("io.ktor:ktor-client-ios:$ktorVersion")
                implementation("io.ktor:ktor-client-core-native:$ktorVersion")
                implementation("io.ktor:ktor-client-logging-native:$ktorVersion")
                implementation("io.ktor:ktor-client-json-native:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization-native:$ktorVersion")
            }
        }
    }
}

// workaround for https://youtrack.jetbrains.com/issue/KT-27170
configurations.create("compileClasspath")

tasks.register<Sync>("packForXCode") {
    val frameworkDir = File(buildDir, "xcode-frameworks")
    val mode = project.findProperty("XCODE_CONFIGURATION") as String? ?: "DEBUG"
    val nativeTarget = kotlin.targets.getByName("ios") as KotlinNativeTarget
    val framework: Framework = nativeTarget.binaries.getFramework("", mode)

    inputs.property("mode", mode)
    dependsOn(framework.linkTask)

    from(framework.outputFile.parentFile)
    into(frameworkDir)

    doLast {
        File(frameworkDir, "gradlew").withGroovyBuilder {
            text("#!/bin/bash\nexport 'JAVA_HOME=${System.getProperty("java.home")}'\ncd '${rootProject.rootDir}'\n./gradlew \$@\n")
        }
    }
}

tasks.build {
    dependsOn("packForXCode")
}
