plugins {
    id("java-library")
    id("astralib-publishing")
}

dependencies {
    api(libs.cloud.core)

    api(libs.gson)
    api(libs.lettuce)
    api(libs.bundles.sadu)
    api(libs.bundles.jackson)
    api(libs.bundles.configurate)

    compileOnly(libs.bundles.adventure)
    compileOnly(libs.mariadb.client)

    testImplementation(platform("org.junit:junit-bom:6.1.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testCompileOnly(libs.jackson.yaml)
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(25)
    }
}
