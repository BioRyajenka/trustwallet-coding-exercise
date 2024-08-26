plugins {
    kotlin("multiplatform")
}

kotlin {
    listOf(
        linuxX64(),
        mingwX64()
    ).forEach {
        it.binaries  {
            executable("console_app") {
                entryPoint = "com.sushencev.tkvs.main"
                runTask?.standardInput = System.`in`
                runTask?.standardOutput = System.out
                runTask?.errorOutput = System.err
            }
        }
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":shared"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(project(":shared"))
            }
        }

        val linuxX64Main by getting
        val mingwX64Main by getting
        val nativeMain by getting
    }
}
