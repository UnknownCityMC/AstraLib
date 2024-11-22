import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    `maven-publish`
    `java-library`
    id("java")
    alias(libs.plugins.pluginyml)
    alias(libs.plugins.shadow)
}

val shadeBasePath = "${rootProject.group}.libs."
val mainClass = "${rootProject.group}.paper.plugin.AstraLibPaperPlugin"


dependencies {
    api(project(":astralib-common"))

    implementation(libs.cloud.paper)
    implementation(libs.cloud.extras)
    implementation(libs.cloud.confirm)

    bukkitLibrary(libs.configurate.yaml)
    bukkitLibrary(libs.configurate.hocon)

    bukkitLibrary(libs.jackson.yaml)
    bukkitLibrary(libs.jackson.toml)

    bukkitLibrary(libs.redis)

    bukkitLibrary(libs.sadu.mysql)
    bukkitLibrary(libs.sadu.mariadb)
    bukkitLibrary(libs.sadu.postgresql)
    bukkitLibrary(libs.sadu.sqlite)
    bukkitLibrary(libs.sadu.queries)
    bukkitLibrary(libs.sadu.datasource)

    compileOnly(libs.adventure.text.minimessage)
    compileOnly(libs.adventure.api)
    compileOnly(libs.papi)
    compileOnly(libs.paper.api)

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(libs.paper.api)
}

tasks {
    shadowJar {
        archiveVersion.set(rootProject.version.toString())
        archiveBaseName.set("AstraLib-Paper")
        archiveClassifier.set("")

        fun relocateDependency(from : String) = relocate(from, "$shadeBasePath$from")
        relocateDependency("org.incendo")
    }

    test {
        useJUnitPlatform()
    }
}

bukkit {

    name = "AstraLib"
    version = "${rootProject.version}"
    description = "A super cool plugin utility library"

    author = "UnknownCity"

    main = mainClass

    foliaSupported = false

    apiVersion = "1.21"

    load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD

    softDepend = listOf("PlaceholderAPI")

    defaultPermission = BukkitPluginDescription.Permission.Default.OP
}


publishing {
    repositories {
        maven {
            name = "UnknownCity"
            url = if(project.version.toString().endsWith("-SNAPSHOT")) {
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
            artifact(tasks["shadowJar"])
            group = rootProject.group
            artifactId = project.name
            version = rootProject.version.toString()
        }
    }
}