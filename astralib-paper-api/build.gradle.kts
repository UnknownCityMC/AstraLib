plugins {
    `maven-publish`
    `java-library`
    alias(libs.plugins.shadow)
}

val shadeBasePath = "${rootProject.group}.libs."

dependencies {
    api(project(":astralib-common"))

    api(libs.cloud.paper)
    api(libs.cloud.extras)
    api(libs.cloud.confirm)

    api(libs.configurate.yaml)

    api(libs.jackson.yaml)
    api(libs.jackson.toml)

    api(libs.redis)

    api(libs.sadu.mysql)
    api(libs.sadu.mariadb)
    api(libs.sadu.postgresql)
    api(libs.sadu.sqlite)
    api(libs.sadu.queries)
    api(libs.sadu.datasource)

    compileOnly(libs.papi)
    compileOnly(libs.paper.api)

    testImplementation(platform("org.junit:junit-bom:5.12.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(libs.paper.api)
}

tasks {
    test {
        useJUnitPlatform()
    }

    shadowJar {
        fun relocateDependency(from : String) = relocate(from, "$shadeBasePath$from")
        relocateDependency("com.fasterxml")


        dependencies {
            exclude("de.chojo")
            exclude("redis")
            exclude("com.google")
            exclude("com.zaxxer")
        }
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
            from(components["shadow"])
            group = rootProject.group
            artifactId = project.name
            version = rootProject.version.toString()
        }
    }
}