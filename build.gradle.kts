import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.test

plugins {
    `maven-publish`
    `java-library`
    alias(libs.plugins.shadow)
    id("java")
}

group = "de.unknowncity.astralib"
version = "0.7.0-SNAPSHOT"

dependencies {
    api(libs.configurate.yaml)
    api(libs.configurate.hocon)

    api(libs.jackson.yaml)
    api(libs.jackson.toml)

    api(libs.cloud.core)
    api(libs.cloud.paper)
    api(libs.cloud.extras)
    api(libs.cloud.confirm)

    testImplementation(platform("org.junit:junit-bom:5.13.2"))
    testImplementation("org.junit.jupiter", "junit-jupiter", "5.10.3")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    test {
        useJUnitPlatform()
    }
}