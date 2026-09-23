plugins {
    `java-library`
    id("astralib-publishing")
}

dependencies {
    api(project(":astralib-common"))

    api(libs.bundles.cloud.paper)

    compileOnly(libs.papi)
    compileOnly(libs.paper.api)

    testImplementation(platform("org.junit:junit-bom:6.1.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(libs.paper.api)
}