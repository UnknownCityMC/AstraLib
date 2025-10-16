plugins {
    id("java")
}

group = "de.unknowncity.astralib"
version = "0.7.0-SNAPSHOT"


dependencies {
    implementation(project(":astralib-velocity-api"))

    compileOnly(libs.velocity)
    annotationProcessor(libs.velocity)
}

tasks.test {
    useJUnitPlatform()
}