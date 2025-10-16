plugins {
    `maven-publish`
    id("java")
    id("java-library")
}

val shadeBasePath = "${group}.${rootProject.name.lowercase()}."

dependencies {
    api(libs.cloud.core)
    api(libs.gson)

    compileOnly(libs.configurate.yaml)
    compileOnly(libs.configurate.hocon)

    api(libs.sadu.mariadb)
    api(libs.sadu.postgresql)
    api(libs.sadu.sqlite)
    api(libs.sadu.queries)
    api(libs.sadu.datasource)

    compileOnly(libs.mariadb.client)
    compileOnly(libs.lettuce)

    compileOnly(libs.jackson.yaml)
    compileOnly(libs.jackson.toml)

    compileOnly(libs.adventure.text.minimessage)
    compileOnly(libs.adventure.api)

    testImplementation(platform("org.junit:junit-bom:5.13.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    testCompileOnly(libs.jackson.yaml)
}


tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }

    jar {
        archiveBaseName.set(rootProject.name)
        archiveVersion.set(rootProject.version.toString())
    }

    test {
        useJUnitPlatform()
    }
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
            from(components["java"])
            group = rootProject.group
            artifactId = project.name
            version = rootProject.version.toString()
        }
    }
}