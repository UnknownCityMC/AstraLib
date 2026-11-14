import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    id("java")
    alias(libs.plugins.pluginyml)
    alias(libs.plugins.run.paper)
    id("com.gradleup.shadow")
    id("astralib-repositories")
}

val mainClass = "${rootProject.group}.paper.plugin.AstraLibPaperPlugin"

repositories {
    maven {
        name = "central-snapshots"
        url = uri("https://central.sonatype.com/repository/maven-snapshots/")
    }
}

dependencies {
    implementation(project(":astralib-common")) {
        exclude(group = "*", module = "*")
    }
    implementation(project(":astralib-paper-api")) {
        exclude(group = "*", module = "*")
    }

    paperLibrary(libs.bundles.cloud.paper)

    paperLibrary(libs.bundles.configurate)

    paperLibrary(libs.lettuce)
    paperLibrary(libs.gson)

    paperLibrary(libs.bundles.sadu)
    paperLibrary(libs.bundles.jackson)
    paperLibrary(libs.ocular)

    compileOnly(libs.bundles.adventure)

    compileOnly(libs.papi)
    compileOnly(libs.paper.api)
}

paper {
    name = "AstraLib"
    version = "${rootProject.version}"
    description = "A super cool plugin utility library"

    author = "UnknownCity"

    main = mainClass
    loader = "${rootProject.group}.paper.plugin.PluginLibrariesLoader"

    foliaSupported = false

    apiVersion = "26.3"

    generateLibrariesJson = true

    load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD

    serverDependencies  {
        register("PlaceholderAPI") {
            required = false
        }
    }

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
    }

    generatePaperPluginDescription {
        useDefaultCentralProxy()
        // PlaceholderAPI is a server plugin, not a runtime library repository
        repos.set(repos.get() - "placeholderapi")
    }

    runServer {
        minecraftVersion("26.3")
        jvmArgs("-Dcom.mojang.eula.agree=true")
    }
}