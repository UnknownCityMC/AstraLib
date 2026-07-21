plugins {
    id("java")
    id("astralib-publishing")
}

val shadeBasePath = "${group}.${rootProject.name.lowercase()}."

dependencies {
    compileOnly(libs.cloud.core)

    compileOnly(libs.configurate.yaml)
    compileOnly(libs.configurate.hocon)

    compileOnly(libs.lettuce)
    compileOnly(libs.gson)

    compileOnly(libs.sadu.mariadb)
    compileOnly(libs.sadu.postgresql)
    compileOnly(libs.sadu.sqlite)
    compileOnly(libs.sadu.queries)
    compileOnly(libs.sadu.datasource)
    compileOnly(libs.mariadb.client)

    compileOnly(libs.jackson.yaml)
    compileOnly(libs.jackson.toml)

    compileOnly(libs.adventure.text.minimessage)
    compileOnly(libs.adventure.api)

    testImplementation(platform("org.junit:junit-bom:6.1.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

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
