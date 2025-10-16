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
            url = if(rootProject.version.toString().endsWith("-SNAPSHOT")) {
                uri("https://repo.unknowncity.de/snapshots")
            } else {
                uri("https://repo.unknowncity.de/releases")
            }

            credentials {
                username = System.getenv("MVN_REPO_USERNAME")
                password = System.getenv("MVN_REPO_PASSWORD")
            }
        }
    }

    publications {
        register<MavenPublication>("maven") {
            from(components["shadow"])
            group = rootProject.group
            artifactId = project.name
            version = rootProject.version.toString()
        }
    }
}