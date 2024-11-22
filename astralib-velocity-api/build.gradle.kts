plugins {
    `maven-publish`
    `java-library`
    id("java")
}

dependencies {
    api(project(":astralib-common"))
}

tasks.test {
    useJUnitPlatform()
}