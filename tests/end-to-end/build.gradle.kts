plugins {
    kotlin("jvm") version "1.9.21"
    idea
    id("com.github.ben-manes.versions") version "0.47.0"
    id("net.serenity-bdd.serenity-gradle-plugin") version "4.0.1"
}

group = "org.hyperledger.identus"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/input-output-hk/atala-automation/")
        credentials {
            username = System.getenv("ATALA_GITHUB_ACTOR")
            password = System.getenv("ATALA_GITHUB_TOKEN")
        }
    }
    maven {
        url = uri("https://maven.pkg.github.com/hyperledger/identus-cloud-agent/")
        credentials {
            username = System.getenv("ATALA_GITHUB_ACTOR")
            password = System.getenv("ATALA_GITHUB_TOKEN")
        }
    }
}

dependencies {
    testImplementation("org.hyperledger.identus:edge-agent-sdk:3.0.0")
    testImplementation("io.iohk.atala.prism:prism-kotlin-client:1.31.0")
    testImplementation("io.iohk.atala:atala-automation:0.3.2")
    testImplementation("app.cash.sqldelight:sqlite-driver:2.0.2")
    testImplementation("io.ktor:ktor-client-core-jvm:2.3.12")
}

tasks.register<Delete>("cleanTarget") {
    delete("target")
}

tasks.test {
    dependsOn("cleanTarget")
    testLogging.showStandardStreams = true
    systemProperty("cucumber.filter.tags", System.getProperty("cucumber.filter.tags"))
}

kotlin {
    jvmToolchain(17)
}
