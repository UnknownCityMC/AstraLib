import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    `maven-publish`
    `java-library`
    id("java")
    alias(libs.plugins.shadow)
    alias(libs.plugins.pluginyml)
}

val shadeBasePath = "${rootProject.group}.libs."
val mainClass = "${rootProject.group}.paper.plugin.AstraLibPaperPlugin"


dependencies {
    api(project(":astralib-common"))

    implementation(libs.cloud.paper)
    implementation(libs.cloud.extras)
    implementation(libs.configurate.yaml)
    implementation(libs.configurate.hocon)
    implementation(libs.redis)
    implementation(libs.sadu.mysql)
    implementation(libs.sadu.mariadb)
    implementation(libs.sadu.postgresql)
    implementation(libs.sadu.sqlite)
    implementation(libs.sadu.queries)
    implementation(libs.sadu.datasource)


    compileOnly(libs.adventure.text.minimessage)
    compileOnly(libs.adventure.api)
    compileOnly(libs.papi)
    compileOnly(libs.paper.api)

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
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