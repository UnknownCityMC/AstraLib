plugins {
    `maven-publish`
    id("java")
}

val shadeBasePath = "${group}.${rootProject.name.lowercase()}."

dependencies {
    compileOnly(libs.cloud.core)

    compileOnly(libs.configurate.yaml)
    compileOnly(libs.configurate.hocon)

    compileOnly(libs.redis)

    compileOnly(libs.sadu.mysql)
    compileOnly(libs.sadu.mariadb)
    compileOnly(libs.sadu.postgresql)
    compileOnly(libs.sadu.sqlite)
    compileOnly(libs.sadu.queries)
    compileOnly(libs.sadu.datasource)

    compileOnly(libs.adventure.text.minimessage)
    compileOnly(libs.adventure.api)

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
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
            artifactId = "astralib-common"
            version = rootProject.version.toString()
        }
    }
}