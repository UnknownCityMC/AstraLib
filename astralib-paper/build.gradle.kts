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

    bukkitLibrary(libs.cloud.paper)
    bukkitLibrary(libs.cloud.extras)

    bukkitLibrary(libs.configurate.yaml)
    bukkitLibrary(libs.configurate.hocon)

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
}

tasks {
    shadowJar {
        archiveVersion.set(rootProject.version.toString())
        archiveBaseName.set("AstraLib")
        archiveAppendix.set("paper")
        archiveClassifier.set("")
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

    libs {

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
            artifactId = "astralib-paper"
            version = rootProject.version.toString()
        }
    }
}