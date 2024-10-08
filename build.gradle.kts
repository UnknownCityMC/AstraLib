import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.test

plugins {
    `maven-publish`
    `java-library`
    alias(libs.plugins.shadow)
    id("java")
}

group = "de.unknowncity.astralib"
version = "0.4.0-SNAPSHOT"

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.3"))
    testImplementation("org.junit.jupiter", "junit-jupiter", "5.10.3")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.test {
    useJUnitPlatform()
}