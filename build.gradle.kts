plugins {
    `project-report`
    id("org.jetbrains.kotlin.jvm") version "1.9.21"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.9.21"
    id("com.google.devtools.ksp") version "1.9.21-1.0.15"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.application") version "4.2.0"
    id("io.micronaut.aot") version "4.2.0"
}

version = "0.0.1"
group = "cloud.softwareag"

val kotlinVersion = project.properties["kotlinVersion"]
val log4jVersion = project.properties["log4jVersion"]
val jacksonVersion = project.properties["jacksonVersion"]

repositories {
    mavenCentral()
    maven { url = uri("https://repository.apache.org/snapshots") }
}

dependencies {
    ksp("io.micronaut:micronaut-http-validation")
    ksp("io.micronaut.serde:micronaut-serde-processor")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("org.apache.logging.log4j:log4j-api:${log4jVersion}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
    compileOnly("io.micronaut:micronaut-http-client")
    runtimeOnly("ch.qos.logback:logback-classic:1.4.14")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin:${jacksonVersion}")
    runtimeOnly("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:${jacksonVersion}")
    runtimeOnly("org.apache.logging.log4j:log4j-core:${log4jVersion}")
    testImplementation("io.micronaut:micronaut-http-client")
}


application {
    mainClass.set("cloud.softwareag.ApplicationKt")
}
java {
    sourceCompatibility = JavaVersion.toVersion("17")
}


graalvmNative.toolchainDetection.set(false)
micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("cloud.softwareag.*")
    }
    aot {
    // Please review carefully the optimizations enabled below
    // Check https://micronaut-projects.github.io/micronaut-aot/latest/guide/ for more details
        optimizeServiceLoading.set(false)
        convertYamlToJava.set(false)
        precomputeOperations.set(true)
        cacheEnvironment.set(true)
        optimizeClassLoading.set(true)
        deduceEnvironment.set(true)
        optimizeNetty.set(true)
    }
}
