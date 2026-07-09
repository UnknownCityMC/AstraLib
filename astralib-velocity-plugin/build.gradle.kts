plugins {
    id("java")
    alias(libs.plugins.run.velocity)
    alias(libs.plugins.shadow)
}

group = "de.unknowncity.astralib"
version = "0.7.0-SNAPSHOT"

val shadeBasePath = "${rootProject.group}.libs."

dependencies {
    implementation(project(":astralib-common"))
    implementation(project(":astralib-velocity-api"))

    implementation(libs.mariadb.client)
    implementation(libs.gson)

    compileOnly(libs.velocity)
    annotationProcessor(libs.velocity)
}

// Keeps the version in the @Plugin annotation in sync with the gradle version
val generateBuildConstants by tasks.registering {
    val version = rootProject.version.toString()
    val outputDir = layout.buildDirectory.dir("generated/sources/buildConstants/java")
    inputs.property("version", version)
    outputs.dir(outputDir)
    doLast {
        val file = outputDir.get().file("de/unknowncity/astralib/velocity/plugin/BuildConstants.java").asFile
        file.parentFile.mkdirs()
        file.writeText(
            """
            package de.unknowncity.astralib.velocity.plugin;

            public final class BuildConstants {
                public static final String VERSION = "$version";

                private BuildConstants() {
                }
            }
            """.trimIndent() + "\n"
        )
    }
}

sourceSets.main {
    java.srcDir(generateBuildConstants)
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        // Velocity 3.5.0 only requires Java 21, keep the plugin usable there
        options.release.set(21)
    }

    // Velocity has no library loader like Bukkit, so everything gets shaded
    shadowJar {
        archiveVersion.set(rootProject.version.toString())
        archiveBaseName.set("AstraLib-Velocity")
        archiveClassifier.set("")

        fun relocateDependency(from : String) = relocate(from, "$shadeBasePath$from")
        relocateDependency("com.fasterxml")
        mergeServiceFiles()
        exclude("META-INF/LICENSE*", "META-INF/NOTICE*")
    }

    runVelocity {
        velocityVersion("3.5.0-SNAPSHOT")
    }

    test {
        useJUnitPlatform()
    }
}
