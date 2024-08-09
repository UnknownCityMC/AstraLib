plugins {
    `maven-publish`
    `java-library`
    id("java")
    alias(libs.plugins.shadow)
}

dependencies {
    api(project(":astralib-common"))
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    repositories {
        maven {
            name = "UnknownCity"
            url = uri("https://repo.unknowncity.de/snapshots")

            credentials {
                username = System.getenv("MVN_REPO_USERNAME")
                password = System.getenv("MVN_REPO_PASSWORD")
            }
        }
    }
    publications {
        register<MavenPublication>("maven") {
            from(components["java"])
            group = rootProject.group
            artifactId = "astralib-velocity"
            version = rootProject.version.toString()
        }
    }
}