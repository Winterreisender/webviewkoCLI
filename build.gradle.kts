plugins {
    kotlin("jvm") version "1.7.0"
    application
    id("com.github.johnrengelman.shadow") version "latest.release"

}


group = "com.github.Winterreisender"
version = "v0.0.1-experimental.15"
description = "webviewko"

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
        content {
            includeGroup("com.github.Winterreisender")
        }
    }
}

tasks.jar {
    manifest {
        attributes(
            mapOf("Implementation-Title" to project.name,
                "Implementation-Version" to project.version)
        )
    }
}

tasks.shadowJar {
    manifest {
        attributes(mapOf(
            "Main-Class" to "com.github.winterreisender.webviewkocli.MainKt",
            "ImplementationTitle" to project.name,
            "Implementation-Version" to project.version)
        )
    }
}

application {
    mainClass.set("com.github.winterreisender.webviewkocli.Main") // THIS JUST NOT WORK
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.4")
    implementation("org.jetbrains.kotlinx:kotlinx-cli-jvm:0.3.4")
    implementation("com.github.Winterreisender:webviewko:v0.0.1-experimental.15")
}