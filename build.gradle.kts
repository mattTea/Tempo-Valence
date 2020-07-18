import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm").version("1.3.50")
    id("application")
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("com.google.cloud.tools.jib").version("2.1.0")
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testCompile("com.willowtreeapps.assertk:assertk-jvm:0.18")

    implementation("org.http4k:http4k-core:3.163.0")
    implementation("org.http4k:http4k-server-jetty:3.163.0")
    implementation("org.http4k:http4k-contract:3.163.0")
    implementation("org.http4k:http4k-format-argo:3.163.0")
    implementation("org.http4k:http4k-cloudnative:3.163.0")
    implementation("org.http4k:http4k-client-okhttp:3.163.0")
    implementation("org.http4k:http4k-client-apache:3.163.0")
    implementation("org.http4k:http4k-format-jackson:3.165.0")

    testImplementation("org.spekframework.spek2:spek-dsl-jvm:2.0.5")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:2.0.5")
    testRuntimeOnly("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("io.mockk:mockk:1.9.3")
}

tasks.withType<Test> {
    useJUnitPlatform {
        includeEngines("spek2")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClassName = "track.search.MainKt"
}

jib {
    container {
        mainClass = application.mainClassName
        environment = mapOf(Pair("CLIENT_KEY", System.getenv("CLIENT_KEY")))
        ports = listOf("8000")
    }
    from {
//        image = "azul/zulu-openjdk-alpine:11"
        image = "openjdk:11"
    }
    to {
        image = "registry.hub.docker.com/matttea/matttea-images/tempo-valence:latest"
//        image = "registry.hub.docker.com/repository/docker/matttea/matttea-images/tempo-valence:latest"
        credHelper = "osxkeychain"
    }
}