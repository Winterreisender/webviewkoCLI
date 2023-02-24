plugins {
    kotlin("multiplatform") version "1.7.20"
}


repositories {
    mavenCentral()
    maven("https://gitlab.com/api/v4/projects/38224197/packages/maven")
}


kotlin {
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported.")
    }
    val osPrefix = when {
        hostOs == "Mac OS X" -> "macosX64"
        hostOs == "Linux" -> "linuxX64"
        isMingwX64 -> "mingwX64"
        else -> throw GradleException("Host OS is not supported.")
    }.toLowerCase()

    nativeTarget.apply {
        binaries {
            executable {
                entryPoint = "com.github.winterreisender.webviewkocli.main"
                if(hostOs == "Linux") {
                    linkerOpts("native/src/nativeMain/resources/linuxx64/libwebview.so","-Wl,-rpath=${'$'}ORIGIN")
                    copy {
                        from(rootDir.resolve("native/src/nativeMain/resources/${osPrefix}"))
                        into(outputDirectory)
                        include("*.so")
                        duplicatesStrategy= DuplicatesStrategy.WARN
                    }
                }
            }
        }
    }
    sourceSets {
        val nativeMain by getting {
            dependencies {
                implementation("com.github.winterreisender:webviewko-${osPrefix}:0.6.0-RC2")
                implementation("org.jetbrains.kotlinx:kotlinx-cli-${osPrefix}:0.3.5")
            }
        }
        val nativeTest by getting
    }
}
