plugins {
    kotlin("jvm") version "1.7.10"
    application
}

//group = "com.github.winterreisender"
//version = "0.2.0"
//description = "webviewko cli"

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/Winterreisender/webviewko")
        credentials {
            username = System.getenv("USERNAME")// ?: error("no USERNAME")
            password = System.getenv("TOKEN")// ?: error("no TOKEN")
        }
    }
}

tasks.jar {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to "com.github.winterreisender.webviewkocli.MainKt",
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version
            )
        )
    }
}

application {
    mainClass.set("com.github.winterreisender.webviewkocli.MainKt")
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
                "Main-Class" to "com.github.winterreisender.webviewkocli.MainKt",
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
    implementation("com.github.winterreisender:webviewko:0.3.0-SNAPSHOT")
    implementation("com.github.winterreisender:webviewko-jvm:0.3.0-SNAPSHOT")
    //{
    //    exclude("net.java.dev.jna","jna")
    //}

}