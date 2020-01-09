import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("kotlinx-serialization")
    application
}

repositories {
    jcenter()
}

dependencies {
    val kotlinVersion: String by project
    val serializationVersion: String by project
    val ktorVersion: String by project

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:$serializationVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-serialization:$ktorVersion")
    implementation("org.slf4j:slf4j-simple:1.6.4")

    implementation(project(":data"))

    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
}

application {
    mainClassName = "ServerKt"
}

// compile bytecode to java 8 (default is java 6)
tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += "-Xjvm-default=enable"
        javaParameters = true
    }
}