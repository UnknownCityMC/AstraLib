plugins {
    `maven-publish`
    id("java")
    alias(libs.plugins.shadow)
}

val shadeBasePath = "${group}.${rootProject.name.lowercase()}."

dependencies {
    implementation(libs.cloud.core)
    implementation(libs.configurate.yaml)
    implementation(libs.configurate.hocon)
    implementation(libs.jackson.toml)
    implementation(libs.redis)
    implementation(libs.sadu.mysql)
    implementation(libs.sadu.mariadb)
    implementation(libs.sadu.postgresql)
    implementation(libs.sadu.sqlite)
    implementation(libs.sadu.queries)
    implementation(libs.sadu.datasource)

    compileOnly(libs.adventure.text.minimessage)
    compileOnly(libs.adventure.api)
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}


tasks {
    shadowJar {
        relocate("redis.clients", shadeBasePath + "redis")
        relocate("org.spongepowered", shadeBasePath + "configurate")
    }

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