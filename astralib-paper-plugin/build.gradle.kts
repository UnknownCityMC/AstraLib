import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    id("java")
    alias(libs.plugins.pluginyml)
    alias(libs.plugins.run.paper)
    id("com.gradleup.shadow")
}

val mainClass = "${rootProject.group}.paper.plugin.AstraLibPaperPlugin"

dependencies {
    implementation(project(":astralib-common"))
    implementation(project(":astralib-paper-api")) {
        exclude(group = "*", module = "*")
    }

    bukkitLibrary(libs.bundles.cloud.paper)

    bukkitLibrary(libs.bundles.configurate)

    bukkitLibrary(libs.lettuce)
    bukkitLibrary(libs.gson)

    implementation(libs.bundles.jackson)

    bukkitLibrary(libs.bundles.sadu)

    compileOnly(libs.bundles.adventure)

    compileOnly(libs.papi)
    compileOnly(libs.paper.api)
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

tasks {
    jar {
        enabled = false;
    }

    shadowJar {
        dependsOn(":astralib-paper-api:build")
        archiveVersion.set(rootProject.version.toString())
        archiveBaseName.set("AstraLib-Paper")

        relocateDependency("com.fasterxml")
    }

    runServer {
        minecraftVersion("26.1.2")
        jvmArgs("-Dcom.mojang.eula.agree=true")
    }
}