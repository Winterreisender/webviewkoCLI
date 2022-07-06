plugins {
    kotlin("multiplatform") version "1.7.0"
}

group = "com.github.winterreisender"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/Winterreisender/webviewko")
        credentials {
            username = System.getenv("USERNAME")// ?: error("no USERNAME")
            password = System.getenv("TOKEN")// ?: error("no TOKEN")
        }
    }
    //mavenLocal()
}


kotlin {
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        //hostOs == "Mac OS X" -> macosX64("native")
        //hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported.")
    }

    nativeTarget.apply {
        binaries {
            executable {
                entryPoint = "com.github.winterreisender.webviewkocli.main"
                if(hostOs == "Linux") linkerOpts("-Wl,-rpath=${'$'}ORIGIN")

                //Copy dll,so to executable file's folder. This does not include debugTest
                copy {
                    from(rootDir.resolve("native/src/nativeMain/resources/"))
                    into(outputDirectory)
                    into(outputDirectory.toPath().parent.resolve("debugTest"))
                    include("*.dll", "*.dylib", "*.so")
                    duplicatesStrategy= DuplicatesStrategy.WARN
                }
            }
        }
    }
    sourceSets {
        val nativeMain by getting {
            dependencies {
                implementation("com.github.winterreisender:webviewko-mingwx64:0.2.0-SNAPSHOT")
                implementation("org.jetbrains.kotlinx:kotlinx-cli-mingwx64:0.3.4")
            }
        }
        val nativeTest by getting
    }
}
