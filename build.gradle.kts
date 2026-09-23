import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("base")
    id("pl.allegro.tech.build.axion-release") version "1.21.2"
    id("com.gradleup.shadow")
}

group = "de.unknowncity.astralib"
version = scmVersion.version

scmVersion {
    tag {
        prefix.set("v")
    }
    snapshotCreator { _, _ -> "-SNAPSHOT" }
}

allprojects {
    version = rootProject.version
    group = rootProject.group
}

subprojects {
    plugins.withId("com.gradleup.shadow") {
        tasks.withType<ShadowJar> {
            archiveClassifier.set("")
            mergeServiceFiles()
            exclude("META-INF/LICENSE*", "META-INF/NOTICE*")
        }
    }

    plugins.withType<JavaPlugin> {
        extensions.configure<JavaPluginExtension> {
            toolchain.languageVersion.set(JavaLanguageVersion.of(25))
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}