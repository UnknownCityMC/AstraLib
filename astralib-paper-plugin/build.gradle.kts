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
        relocateDependency("org.incendo")
        relocateDependency("io.leangen")
    }
}