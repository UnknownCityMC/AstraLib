plugins {
    id("base")
}

group = "de.unknowncity.astralib"
version = "0.8.0-SNAPSHOT"

subprojects {
    plugins.withType<JavaPlugin> {
        extensions.configure<JavaPluginExtension> {
            toolchain.languageVersion.set(JavaLanguageVersion.of(25))
        }
    }
}