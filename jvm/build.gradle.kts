plugins {
    kotlin("jvm") version "1.7.0"
    application
}

group = "com.github.Winterreisender"
version = "0.1.1"
description = "webviewko"

repositories {
    mavenLocal()
    mavenCentral()
    //maven {
    //    url = uri("https://maven.pkg.github.com/Winterreisender/webviewko")
    //}

    maven {
        url = uri("https://jitpack.io")
        content {
            includeGroup("com.github.Winterreisender.webviewko")
        }
    }
}

tasks.jar {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to "com.github.winterreisender.webviewkocli.Main",
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version
            )
        )
    }
}



application {
    mainClass.set("com.github.winterreisender.webviewkocli.Main") // THIS JUST NOT WORK
}


tasks.register<Jar>("uberJar") {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    archiveClassifier.set("uber")
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })

    manifest {
        attributes(
            mapOf(
                "Main-Class" to "com.github.winterreisender.webviewkocli.Main",
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version
            )
        )
    }
}

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

val compileTestKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.4")
    implementation("org.jetbrains.kotlinx:kotlinx-cli-jvm:0.3.4")
    implementation("com.github.Winterreisender.webviewko:webviewko:0.2.0-dev.2")
    implementation("com.github.Winterreisender.webviewko:webviewko-jvm:0.2.0-dev.2")
    //{
    //    exclude("net.java.dev.jna","jna")
    //}

}