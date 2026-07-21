plugins {
    `java-library`
    alias(libs.plugins.shadow)
    id("astralib-publishing")
}

val shadeBasePath = "${rootProject.group}.libs."

dependencies {
    api(project(":astralib-common"))

    api(libs.cloud.velocity)
    api(libs.cloud.extras)
    api(libs.cloud.confirm)

    api(libs.configurate.yaml)

    api(libs.jackson.yaml)
    api(libs.jackson.toml)

    api(libs.lettuce)

    api(libs.sadu.mariadb)
    api(libs.sadu.postgresql)
    api(libs.sadu.sqlite)
    api(libs.sadu.queries)
    api(libs.sadu.datasource)

    compileOnly(libs.velocity)

    testImplementation(platform("org.junit:junit-bom:6.1.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(libs.velocity)
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        // Velocity 3.5.0 only requires Java 21, keep the API usable there
        options.release.set(21)
    }

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
