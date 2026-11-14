plugins {
    id("java-library")
    id("astralib-publishing")
    id("astralib-repositories")
}

dependencies {
    api(libs.cloud.core)

    api(libs.gson)
    api(libs.lettuce)
    api(libs.bundles.sadu)
    api(libs.bundles.jackson)
    api(libs.bundles.configurate)
    api(libs.ocular)

    compileOnly(libs.bundles.adventure)
    compileOnly(libs.mariadb.client)


    testImplementation(platform("org.junit:junit-bom:6.1.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(25)
    }
}
