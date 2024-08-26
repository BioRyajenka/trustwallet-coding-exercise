import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    kotlin("multiplatform")
}

kotlin {
    jvmToolchain(17)

    jvm {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        mainRun {
            mainClass = "com.sushencev.tkvs.MainKt"
        }
        withJava()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":shared"))
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(project(":shared"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting
    }
}

tasks.register<JavaExec>("runJvmApp") {
    mainClass.set("com.sushencev.tkvs.MainKt")
    classpath = sourceSets["main"].runtimeClasspath
    standardInput = System.`in`

    dependsOn("build")
}
