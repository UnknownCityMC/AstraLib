plugins {
    `maven-publish`
}

publishing {
    repositories {
        maven {
            name = "UnknownCity"
            url = if (rootProject.version.toString().endsWith("-SNAPSHOT")) {
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
            group = rootProject.group
            artifactId = project.name
            version = rootProject.version.toString()
        }
    }
}

// Shadowed modules publish the shadow component, plain modules the java component
afterEvaluate {
    publishing.publications.named<MavenPublication>("maven") {
        from(if (plugins.hasPlugin("com.gradleup.shadow")) components["shadow"] else components["java"])
    }
}
