import java.util.Properties

val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val postgresqlDriverVersion: String by project
val exposedVersion: String by project
val arrowVersion: String by project
val kotestVersion: String by project

plugins {
    application
    kotlin("jvm") version "2.0.0"
    id("io.ktor.plugin") version "2.3.11"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0"

    // Lint
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

group = "pt.isel.ps"
version = "0.0.1"

application {
    mainClass.set("pt.isel.ps.energysales.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-host-common-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-resources:$ktorVersion")

    // Serialization
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-client-content-negotiation-jvm")

    // PSQL
    implementation("org.postgresql:postgresql:$postgresqlDriverVersion")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")

    // Security
    implementation("org.springframework.security:spring-security-core:6.3.1")

    // Logging
    implementation("io.ktor:ktor-server-call-logging-jvm")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    // SwaggerUI
    implementation("io.ktor:ktor-server-swagger-jvm")
    implementation("io.ktor:ktor-server-auth-jvm")
    implementation("io.ktor:ktor-server-auth-jwt-jvm")

    // HTTP
    implementation("io.ktor:ktor-server-default-headers-jvm")
    implementation("io.ktor:ktor-server-status-pages-jvm")
    implementation("io.ktor:ktor-server-cors:$ktorVersion")

    // Arrow - Functional Programming Library
    implementation("io.arrow-kt:arrow-core:$arrowVersion")
    implementation("io.arrow-kt:arrow-fx-coroutines:$arrowVersion")

    // Email Service
    implementation("org.simplejavamail:simple-java-mail:8.11.2")

    // Testing
    testImplementation("io.ktor:ktor-server-tests-jvm:$kotlinVersion")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
    testImplementation("io.kotest:kotest-property:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest.extensions:kotest-assertions-ktor:2.0.0")
    testImplementation("io.mockk:mockk:1.13.10")
}

tasks {
    "run"(JavaExec::class) {
        // Add logging to see when the task starts
        doFirst {
            println("Starting the 'run' task")

            val envFile = file("../../.env")
            if (envFile.exists()) {
                println("'.env' file found at: ${envFile.absolutePath}")
                val properties = Properties()
                envFile.inputStream().use { properties.load(it) }
                properties.forEach { key, value ->
                    val keyStr = key.toString()
                    val valueStr = value.toString()
                    project.extra[keyStr] = valueStr
                    environment(keyStr, valueStr)
                    // Log each environment variable being set
                    println("Setting environment variable: $keyStr=$valueStr")
                }
            } else {
                println("No .env file found at: ${envFile.absolutePath}")
            }
        }

        doLast {
            println("'run' task finished")
        }
    }
    "test"(Test::class) {
        // Add logging to see when the task starts
        doFirst {
            println("Starting the 'run' task")

            val envFile = file("../../.env")
            if (envFile.exists()) {
                println("'.env' file found at: ${envFile.absolutePath}")
                val properties = Properties()
                envFile.inputStream().use { properties.load(it) }
                properties.forEach { key, value ->
                    val keyStr = key.toString()
                    val valueStr = value.toString()
                    project.extra[keyStr] = valueStr
                    environment(keyStr, valueStr)
                    // Log each environment variable being set
                    println("Setting environment variable: $keyStr=$valueStr")
                }
            } else {
                println("No .env file found at: ${envFile.absolutePath}")
            }
        }

        doLast {
            println("'run' task finished")
        }
    }
}

task<Exec>("dbTestsUp") {
    print("Starting docker-compose")

    commandLine(
        "docker-compose",
        "-f",
        "../../docker-compose-test.yml",
        "up",
        "-d",
        "--build",
        "--force-recreate",
    )
}

task<Exec>("dbTestsDown") {
    commandLine("docker-compose", "down")
}

tasks {
// Ensure database container is up and ready before running tests
    named<Test>("test") {
        dependsOn("dbTestsUp")
        finalizedBy("dbTestsDown")
    }
}
