import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    id("java")
    alias(libs.plugins.pluginyml)
    alias(libs.plugins.run.paper)
    alias(libs.plugins.shadow)
}

val mainClass = "${rootProject.group}.paper.plugin.AstraLibPaperPlugin"
val shadeBasePath = "${rootProject.group}.libs."

dependencies {
    implementation(project(":astralib-common"))
    implementation(project(":astralib-paper-api")) {
        exclude(group = "*", module = "*")
    }

    bukkitLibrary(libs.cloud.paper)
    bukkitLibrary(libs.cloud.extras)
    bukkitLibrary(libs.cloud.confirm)

    bukkitLibrary(libs.configurate.yaml)
    bukkitLibrary(libs.configurate.hocon)

    bukkitLibrary(libs.redis)

    implementation(libs.jackson.yaml)
    implementation(libs.jackson.toml)

    bukkitLibrary(libs.sadu.mysql)
    bukkitLibrary(libs.sadu.mariadb)
    bukkitLibrary(libs.sadu.postgresql)
    bukkitLibrary(libs.sadu.sqlite)
    bukkitLibrary(libs.sadu.queries)
    bukkitLibrary(libs.sadu.datasource)
    bukkitLibrary(libs.mariadb.client)

    compileOnly(libs.adventure.text.minimessage)
    compileOnly(libs.adventure.api)
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
    shadowJar {
        archiveVersion.set(rootProject.version.toString())
        archiveBaseName.set("AstraLib-Paper")
        archiveClassifier.set("")

        fun relocateDependency(from : String) = relocate(from, "$shadeBasePath$from")
        relocateDependency("com.fasterxml")
    }

    runServer {
        minecraftVersion("1.21.5")
        jvmArgs("-Dcom.mojang.eula.agree=true")
    }
}