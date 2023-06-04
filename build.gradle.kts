plugins {
    kotlin("jvm") version "1.9.0-Beta"
    application
    id("org.jlleitschuh.gradle.ktlint") version "11.3.2"
}

group = "dev.hermannm"
version = "0.1.0"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    testImplementation(kotlin("test"))
}

sourceSets {
    main {
        kotlin.setSrcDirs(listOf("src"))
        resources.setSrcDirs(listOf("resources"))
    }
    test {
        kotlin.setSrcDirs(listOf("test"))
        resources.setSrcDirs(listOf("test-resources"))
    }
}

application {
    mainClass.set("dev.hermannm.monkeylang.ReplKt")
}

// Configures stdin for application
tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

tasks.test {
    testLogging {
        showStandardStreams = true
        events("passed", "skipped", "failed")
    }

    useJUnitPlatform()
}
