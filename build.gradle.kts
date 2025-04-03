import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
    id("io.ktor.plugin") version "2.3.3"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0"
}

group = "com.onlinemarketplace"
version = "0.0.1"

application {
    mainClass.set("com.onlinemarketplace.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor core dependencies
    implementation("io.ktor:ktor-server-core-jvm:2.3.3")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.3")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.3.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.3.3")
    implementation("io.ktor:ktor-server-cors-jvm:2.3.3")
    implementation("io.ktor:ktor-server-auth-jvm:2.3.3")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:2.3.3")
    
    // Database
    implementation("org.litote.kmongo:kmongo-coroutine:4.9.0")
    implementation("org.mongodb:mongodb-driver-reactivestreams:4.9.0")
    implementation("org.mongodb:mongodb-driver-core:4.9.0")
    implementation("org.mongodb:bson:4.9.0")
    
    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.11")
    
    // Testing
    testImplementation("io.ktor:ktor-server-test-host-jvm:2.3.3")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.0")
    
    // Security
    implementation("commons-codec:commons-codec:1.16.0")
    implementation("org.mindrot:jbcrypt:0.4")
    
    // Email
    implementation("com.sun.mail:javax.mail:1.6.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
} 